package com.phouthasak.controlHQ.service.kasa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phouthasak.controlHQ.model.dto.kasa.Device;
import com.phouthasak.controlHQ.util.KasaCipher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Service
@Slf4j
public class KasaRequestService {
    private static final int KASA_PORT = 9999;
    private static final int TIMEOUT_MS = 5000;
    private static final int PAYLOAD_LOWER_LIMIT = 0;
    private static final int PAYLOAD_UPPER_LIMIT = 65536;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SocketFactory socketFactory;

    public String sendCommand(String ip, String command) throws IOException {
        try (Socket socket = socketFactory.createSocket()) {
            socket.connect(new InetSocketAddress(ip, KASA_PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            byte[] encryptedCommand = KasaCipher.encrypt(command);
            out.write(encryptedCommand);
            out.flush();

            byte[] lengthHeader = readNBytes(in, 4);

            int payloadLength = ((lengthHeader[0] & 0xFF) << 24) |
                    ((lengthHeader[1] & 0xFF) << 16) |
                    ((lengthHeader[2] & 0xFF) << 8) |
                    (lengthHeader[3] & 0xFF);

            if (payloadLength < PAYLOAD_LOWER_LIMIT || payloadLength > PAYLOAD_UPPER_LIMIT) {
                throw new IOException("Invalid payload length: " + payloadLength);
            }

            byte[] payload = readNBytes(in, payloadLength);

            byte[] fullResponse = new byte[4 + payloadLength];
            System.arraycopy(lengthHeader, 0, fullResponse, 0, 4);
            System.arraycopy(payload, 0, fullResponse, 4, payloadLength);

            return KasaCipher.decrypt(fullResponse);
        } catch (IOException ex) {
            log.error("Failed to communicate with Kasa device at IP: {}", ip, ex);
            throw new IOException("Error occur while trying to communicate with device");
        }
    }

    private byte[] readNBytes(InputStream in, int n) throws IOException {
        byte[] buffer = new byte[n];
        int bytesRead = 0;
        while (bytesRead < n) {
            int read = in.read(buffer, bytesRead, n - bytesRead);
            if (read == -1) {
                throw new IOException("Connection closed unexpectedly while reading data.");
            }
            bytesRead += read;
        }
        return buffer;
    }

    public Device parseSystemInfoResponse(String response) {
        try {
            JsonNode rootNode = mapper.readTree(response);
            JsonNode systemInfoNode = rootNode.path("system").path("get_sysinfo");

            if (systemInfoNode.isMissingNode()) {
                log.warn("Kasa response is missing 'system.get_sysinfo' node: {}", response);
                return null;
            }

            Device device = Device.builder()
                    .id(systemInfoNode.path("deviceId").asText(null))
                    .model(systemInfoNode.path("model").asText(null))
                    .name(systemInfoNode.path("alias").asText(null))
                    .latitude(systemInfoNode.path("latitude_i").asLong(0))
                    .longitude(systemInfoNode.path("longitude_i").asLong(0))
                    .relayState(systemInfoNode.path("relay_state").asInt())
                    .errorCode(systemInfoNode.path("err_code").asInt())
                    .build();

            return device;
        } catch (JsonProcessingException ex) {
            log.error("Error parsing Kasa JSON response: {}", response, ex);
            return null;
        }
    }
}

package com.phouthasak.controlHQ.service.kasa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phouthasak.controlHQ.domain.kasa.KasaPayloadBuilder;
import com.phouthasak.controlHQ.model.dto.KasaDto;
import com.phouthasak.controlHQ.model.dto.KasaSmartPlugSystemInfoResponse;
import com.phouthasak.controlHQ.model.dto.KasaSmartPlugSystemInfoResponse.Device;
import com.phouthasak.controlHQ.model.dto.KasaSmartPlugSystemInfoResponse.DeviceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class KasaService {
    private static final int KASA_PORT = 9999;
    private static final int TIMEOUT_MS = 5000;
    private static final int PAYLOAD_LOWER_LIMIT = 0;
    private static final int PAYLOAD_UPPER_LIMIT = 65536;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${KASA_SMART_PLUG_IP}")
    private String KASA_SMART_PLUG_IP;

    public DeviceInfo getDeviceInfo() {
        try {
            String payload = KasaPayloadBuilder.getDeviceInfoPayload();
            String result = sendCommand(payload);
            DeviceInfo deviceInfo = parseSystemInfoResponse(result);
            return deviceInfo;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public DeviceInfo setRelayState(KasaDto dto) {
        try {
            String payload = KasaPayloadBuilder.getReplayStatePayload(Objects.nonNull(dto) && dto.getRelayState());
            sendCommand(payload);
            return getDeviceInfo();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private String sendCommand(String command) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(KASA_SMART_PLUG_IP, KASA_PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            byte[] encryptedCommand = encrypt(command);
            out.write(encryptedCommand);
            out.flush();

            try (ByteArrayOutputStream response = new ByteArrayOutputStream()) {
                byte[] lengthHeader = new byte[4];
                int bytesRead = 0;
                while (bytesRead < 4) {
                    int read = in.read(lengthHeader, bytesRead, 4 - bytesRead);
                    if (read == -1) {
                        throw new IOException("Connection closed while reading");
                    }

                    bytesRead += read;
                }

                int payloadLength = ((lengthHeader[0] & 0xFF) << 24) |
                        ((lengthHeader[1] & 0xFF) << 16) |
                        ((lengthHeader[2] & 0xFF) << 8) |
                        (lengthHeader[3] & 0xFF);

                if (payloadLength < PAYLOAD_LOWER_LIMIT || payloadLength > PAYLOAD_UPPER_LIMIT) {
                    throw new IOException("Invalid payload length: " + payloadLength);
                }

                byte[] payload = new byte[payloadLength];
                bytesRead = 0;
                while(bytesRead < payloadLength) {
                    int read = in.read(payload, bytesRead, payloadLength);
                    if (read == -1) throw new IOException("Connection closed while reading payload");
                    bytesRead += read;
                }

                response.write(lengthHeader);
                response.write(payload);
                return decrypt(response.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private DeviceInfo parseSystemInfoResponse(String response) {
        try {
            JsonNode rootNode = mapper.readTree(response);
            JsonNode systemInfoNode = rootNode.path("system").path("get_sysinfo");

            if (systemInfoNode.isMissingNode()) {
                return null;
            }

            Device device = Device.builder()
                    .deviceId(systemInfoNode.path("deviceId").asText(null))
                    .model(systemInfoNode.path("model").asText(null))
                    .name(systemInfoNode.path("alias").asText(null))
                    .latitude(systemInfoNode.path("latitude_i").asLong(0))
                    .longitude(systemInfoNode.path("longitude_i").asLong(0))
                    .relayState(systemInfoNode.path("relay_state").asInt())
                    .build();

            DeviceInfo deviceInfo = DeviceInfo.builder()
                    .device(device)
                    .errorCode(systemInfoNode.path("err_code").asInt())
                    .build();

            return deviceInfo;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static byte[] encrypt(String plainText) {
        byte[] key = new byte[]{(byte) 0xAB};
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[plainBytes.length + 4];

        int length = plainBytes.length;
        encrypted[0] = (byte) ((length >> 24) & 0xFF);
        encrypted[1] = (byte) ((length >> 16) & 0xFF);
        encrypted[2] = (byte) ((length >> 8) & 0xFF);
        encrypted[3] = (byte) (length & 0xFF);

        byte currentKey = key[0];
        for (int i = 0; i < plainBytes.length; i++) {
            encrypted[i + 4] = (byte) (plainBytes[i] ^ currentKey);
            currentKey = encrypted[i + 4];
        }

        return encrypted;
    }

    private static String decrypt(byte[] encrypted) {
        if (encrypted.length < 4) {
            return "";
        }

        byte[] payload = new byte[encrypted.length - 4];
        System.arraycopy(encrypted, 4, payload, 0, payload.length);

        byte key = (byte) 0xAB;
        byte[] decrypted = new byte[payload.length];

        for (int i = 0; i < payload.length; i++) {
            decrypted[i] = (byte) (payload[i] ^ key);
            key = payload[i];
        }

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}

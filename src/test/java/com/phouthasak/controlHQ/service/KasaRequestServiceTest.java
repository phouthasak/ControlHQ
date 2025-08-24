package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.model.dto.kasa.Device;
import com.phouthasak.controlHQ.service.kasa.KasaRequestService;
import com.phouthasak.controlHQ.service.kasa.SocketFactory;
import com.phouthasak.controlHQ.util.KasaCipher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KasaRequestServiceTest {

    @Mock
    private SocketFactory socketFactory;

    @Mock
    private Socket socket;

    @InjectMocks
    private KasaRequestService kasaRequestService;

    private ByteArrayOutputStream mockOutputStream;

    void setUp() throws IOException {
        when(socketFactory.createSocket()).thenReturn(socket);
        mockOutputStream = new ByteArrayOutputStream();
    }

    @Test
    void sendCommand_whenSuccessful_shouldReturnDecryptedResponse() throws IOException {
        setUp();
        String ip = "192.168.1.100";
        String command = "{\"system\":{\"get_sysinfo\":{}}}";
        String expectedJsonResponse = "{\"system\":{\"get_sysinfo\":{\"alias\":\"My Smart Plug\"}}}";

        byte[] encryptedResponse = KasaCipher.encrypt(expectedJsonResponse);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(encryptedResponse);
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        when(socket.getInputStream()).thenReturn(mockInputStream);

        String actualResponse = kasaRequestService.sendCommand(ip, command);

        assertEquals(expectedJsonResponse, actualResponse);
        byte[] expectedEncryptedCommand = KasaCipher.encrypt(command);
        assertArrayEquals(expectedEncryptedCommand, mockOutputStream.toByteArray());

        verify(socket).connect(any(), eq(5000));
        verify(socket).setSoTimeout(5000);
    }

    @Test
    void sendCommand_whenConnectionFails_shouldThrowIOException() throws IOException {
        setUp();
        String ip = "192.168.1.101";
        String command = "{\"system\":{\"get_sysinfo\":{}}}";
        doThrow(new SocketTimeoutException("Connection failed")).when(socket).connect(any(), anyInt());

        IOException exception = assertThrows(IOException.class, () -> kasaRequestService.sendCommand(ip, command));

        assertInstanceOf(IOException.class, exception);
    }

    @Test
    void sendCommand_whenDeviceReturnsInvalidPayloadLength_shouldThrowIOException() throws IOException {
        setUp();
        String ip = "192.168.1.102";
        String command = "{\"system\":{\"get_sysinfo\":{}}}";
        byte[] malformedHeader = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(malformedHeader);
        when(socket.getOutputStream()).thenReturn(mockOutputStream);
        when(socket.getInputStream()).thenReturn(mockInputStream);

        IOException exception = assertThrows(IOException.class, () -> kasaRequestService.sendCommand(ip, command));
        assertInstanceOf(IOException.class, exception);
    }

    @Test
    void parseSystemInfoResponse_withValidJson_shouldReturnDevice() {
        String validResponse = "{\"system\":{\"get_sysinfo\":{\"err_code\":0,\"deviceId\":\"test-id\",\"alias\":\"Test Plug\",\"model\":\"HS100(US)\",\"relay_state\":1}}}";
        Device device = kasaRequestService.parseSystemInfoResponse(validResponse);
        assertNotNull(device);
        assertEquals("test-id", device.getId());
        assertEquals("Test Plug", device.getName());
        assertEquals("HS100(US)", device.getModel());
        assertEquals(1, device.getRelayState());
        assertTrue(device.isOn());
    }

    @Test
    void parseSystemInfoResponse_withMissingNode_shouldReturnNull() {
        String responseWithMissingNode = "{\"emeter\":{\"get_realtime\":{}}}";
        Device device = kasaRequestService.parseSystemInfoResponse(responseWithMissingNode);
        assertNull(device);
    }

    @Test
    void parseSystemInfoResponse_withInvalidJson_shouldReturnNull() {
        String invalidJson = "this is not json {";
        Device device = kasaRequestService.parseSystemInfoResponse(invalidJson);
        assertNull(device);
    }
}
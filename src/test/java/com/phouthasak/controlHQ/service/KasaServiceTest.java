package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.exception.InternalException;
import com.phouthasak.controlHQ.exception.InvalidException;
import com.phouthasak.controlHQ.model.dto.kasa.Device;
import com.phouthasak.controlHQ.model.dto.kasa.KasaDto;
import com.phouthasak.controlHQ.service.kasa.KasaRequestService;
import com.phouthasak.controlHQ.service.kasa.KasaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KasaServiceTest {
    @Mock
    private KasaRequestService kasaRequestService;

    @InjectMocks
    private KasaService kasaService;

    @BeforeEach
    public void setup() throws IOException {
        String mockIps = "192.168.1.10,192.168.1.20";
        ReflectionTestUtils.setField(kasaService, "KASA_SMART_PLUG_IPS", mockIps);

        Device device1 = Device.builder().id("d1").relayState(0).name("Office Plug").build();
        when(kasaRequestService.sendCommand(eq("192.168.1.10"), anyString())).thenReturn("json-response-1");
        when(kasaRequestService.parseSystemInfoResponse("json-response-1")).thenReturn(device1);

        Device device2 = Device.builder().id("d2").relayState(0).name("Living Room Plug").build();
        when(kasaRequestService.sendCommand(eq("192.168.1.20"), anyString())).thenReturn("json-response-2");
        when(kasaRequestService.parseSystemInfoResponse("json-response-2")).thenReturn(device2);

        ReflectionTestUtils.invokeMethod(kasaService, "init");
    }

    @Test
    void listDevices_shouldReturnAllDevicesInitializedFromIps() throws IOException {
        Device device1Updated = Device.builder().id("d1").name("Office Plug").relayState(1).build();
        when(kasaRequestService.sendCommand(eq("192.168.1.10"), anyString())).thenReturn("json-response-1-updated");
        when(kasaRequestService.parseSystemInfoResponse("json-response-1-updated")).thenReturn(device1Updated);

        Device device2Updated = Device.builder().id("d2").name("Living Room Plug").relayState(0).build();
        when(kasaRequestService.sendCommand(eq("192.168.1.20"), anyString())).thenReturn("json-response-2-updated");
        when(kasaRequestService.parseSystemInfoResponse("json-response-2-updated")).thenReturn(device2Updated);

        List<Device> devices = kasaService.listDevices();

        assertNotNull(devices);
        assertEquals(2, devices.size());
        assertEquals("d1", devices.get(0).getId());
        assertEquals("d2", devices.get(1).getId());
        verify(kasaRequestService, times(2)).sendCommand(eq("192.168.1.10"), anyString());
        verify(kasaRequestService, times(2)).sendCommand(eq("192.168.1.20"), anyString());
    }

    @Test
    void getDevice_whenDeviceNotFound_shouldThrowInvalidException() {
        String invalidDeviceId = "non-existent-device";

        InvalidException exception = assertThrows(InvalidException.class, () -> {
            kasaService.getDevice(invalidDeviceId);
        });

        assertEquals("Invalid Device", exception.getMessage());
    }

    @Test
    void getDevice_whenDeviceFoundErrorOccur_shouldThrowInternalException() throws IOException {
        String errorDevice = "d2";

        when(kasaRequestService.sendCommand(eq("192.168.1.20"), anyString())).thenThrow(new IOException());

        InternalException exception = assertThrows(InternalException.class, () -> {
            kasaService.getDevice(errorDevice);
        });

        assertEquals("Internal Error", exception.getMessage());
    }

    @Test
    void setRelayState_shouldUpdateRelateState() throws IOException {
        String deviceId = "d1";
        KasaDto kasaDto = new KasaDto();
        kasaDto.setRelayState(false);

        Device device1Updated = Device.builder().id("d1").name("Office Plug").relayState(1).build();
        when(kasaRequestService.sendCommand(eq("192.168.1.10"), anyString())).thenReturn("json-response-1-updated");
        when(kasaRequestService.parseSystemInfoResponse("json-response-1-updated")).thenReturn(device1Updated);
        Device device = kasaService.setRelayState(deviceId, kasaDto);

        assertNotNull(device);
        assertEquals("d1", device.getId());
        verify(kasaRequestService, times(3)).sendCommand(eq("192.168.1.10"), anyString());
    }

    @Test
    void setRelayState_whenDeviceFoundErrorOccur_shouldThrowInternalException() throws IOException {
        String errorDevice = "d2";
        KasaDto kasaDto = new KasaDto();
        kasaDto.setRelayState(false);

        when(kasaRequestService.sendCommand(eq("192.168.1.20"), anyString())).thenThrow(new IOException());

        InternalException exception = assertThrows(InternalException.class, () -> {
            kasaService.setRelayState(errorDevice, kasaDto);
        });

        assertEquals("Error setting relay", exception.getMessage());
    }
}

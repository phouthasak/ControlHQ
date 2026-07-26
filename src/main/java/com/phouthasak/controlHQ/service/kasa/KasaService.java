package com.phouthasak.controlHQ.service.kasa;

import com.phouthasak.controlHQ.domain.kasa.KasaPayloadBuilder;
import com.phouthasak.controlHQ.domain.DeviceStatus;
import com.phouthasak.controlHQ.domain.DeviceType;
import com.phouthasak.controlHQ.exception.InternalException;
import com.phouthasak.controlHQ.exception.InvalidException;
import com.phouthasak.controlHQ.model.dto.Device;
import com.phouthasak.controlHQ.model.dto.kasa.KasaDto;
import com.phouthasak.controlHQ.service.EnvironmentService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class KasaService {
    private Map<String, String> deviceMap;

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private KasaRequestService kasaRequestService;

    @PostConstruct
    private void init() {
        deviceMap = new HashMap<>();
        List<String> deviceIps = environmentService.getKasaIps();

        for (String deviceIp : deviceIps) {
            String deviceId = null;
            try {
                String result = kasaRequestService.sendCommand(deviceIp, KasaPayloadBuilder.getDeviceInfoPayload());
                Device device = kasaRequestService.parseSystemInfoResponse(result);
                if (device != null) {
                    deviceId = device.getId();
                    log.info("Successfully connected to Kasa device at {} during startup", deviceIp);
                } else {
                    throw new IOException("Received null response from device");
                }
            } catch (IOException ex) {
                log.warn("Failed to connect to Kasa device at {} during startup. Marking as offline.", deviceIp, ex);
            }
            if (deviceId == null) {
                deviceId = UUID.randomUUID().toString();
            }
            deviceMap.put(deviceId, deviceIp);
        }
    }

    public List<Device> listDevices() {
        List<Device> deviceInfos = new ArrayList<>();

        for (Map.Entry<String, String> entry : deviceMap.entrySet()) {
            String deviceId = entry.getKey();
            String ip = entry.getValue();
            Device device = null;
            try {
                String result = kasaRequestService.sendCommand(ip, KasaPayloadBuilder.getDeviceInfoPayload());
                device = kasaRequestService.parseSystemInfoResponse(result);
                if (device != null) {
                    device.setId(deviceId);
                    device.setStatus(DeviceStatus.SUCCESS);
                }
            } catch (IOException ex) {
                log.error("Error updating Kasa device state for IP: " + ip, ex);
            }

            if (device == null) {
                device = Device.builder()
                        .id(deviceId)
                        .status(DeviceStatus.FAILED)
                        .build();
            }
            deviceInfos.add(device);
        }

        return deviceInfos;
    }

    public Device getDevice(String deviceId) {
        String ip = deviceMap.get(deviceId);

        if (ip == null) {
            throw new InvalidException("Invalid Device");
        }

        try {
            String result = kasaRequestService.sendCommand(ip, KasaPayloadBuilder.getDeviceInfoPayload());
            Device device = kasaRequestService.parseSystemInfoResponse(result);
            if (device != null) {
                device.setId(deviceId);
                device.setStatus(DeviceStatus.SUCCESS);
                return device;
            } else {
                throw new IOException("Received null response from device");
            }
        } catch (IOException ex) {
            log.error("Error getting device info: " + deviceId, ex);
            throw new InternalException("Internal Error");
        }
    }

    public Device setRelayState(Device device, KasaDto dto) {
        if (Objects.isNull(device) || !deviceMap.containsKey(device.getId())) {
            throw new InvalidException("Invalid Device");
        }

        try {
            String payload = KasaPayloadBuilder.getReplayStatePayload(Objects.nonNull(dto) && dto.getRelayState());
            String ip = deviceMap.get(device.getId());
            kasaRequestService.sendCommand(ip, payload);
        } catch (Exception exception) {
            throw new InternalException("Error setting relay");
        }

        device = getDevice(device.getId());
        return device;
    }
}

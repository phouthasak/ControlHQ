package com.phouthasak.controlHQ.service.kasa;

import com.phouthasak.controlHQ.domain.kasa.KasaPayloadBuilder;
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
            Device device = null;
            try {
                String result = kasaRequestService.sendCommand(deviceIp, KasaPayloadBuilder.getDeviceInfoPayload());
                device = kasaRequestService.parseSystemInfoResponse(result);
            } catch (IOException ex) {
                log.error("Error setting up device info map: " + deviceIp, ex);
            }

            if (Objects.nonNull(device)) {
                deviceMap.put(device.getId(), deviceIp);
            }
        }
    }

    public List<Device> listDevices() {
        List<Device> deviceInfos = new ArrayList<>();

        try {
            List<String> deviceIds = new ArrayList<>(deviceMap.keySet());
            for (String deviceId : deviceIds) {
                Device device = getDevice(deviceId);
                deviceInfos.add(device);
            }
        } catch (Exception ex) {
            log.error("Error getting list of devices: ", ex);
        }

        return deviceInfos;
    }

    public Device getDevice(String deviceId) {
        try {
            String payload = KasaPayloadBuilder.getDeviceInfoPayload();
            String ip = deviceMap.get(deviceId);

            if (ip == null) {
                throw new InvalidException("Invalid Device");
            }

            String result = kasaRequestService.sendCommand(ip, payload);
            return kasaRequestService.parseSystemInfoResponse(result);
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

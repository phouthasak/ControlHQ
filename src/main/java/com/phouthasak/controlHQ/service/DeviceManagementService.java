package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.model.dto.Device;
import com.phouthasak.controlHQ.service.kasa.KasaService;
import com.phouthasak.controlHQ.service.tapo.TapoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceManagementService {
    @Autowired
    private KasaService kasaService;

    @Autowired
    private TapoService tapoService;

    private Map<String, Device> deviceMap;

    @PostConstruct
    private void init() {
        refreshDevices();
    }

    public List<Device> getDevices() {
        return deviceMap.values().stream().toList();
    }

    public Device getDeviceInfo(String deviceId) {
        return deviceMap.get(deviceId);
    }

    public void refreshDevices() {
        deviceMap = new HashMap<>();

        List<Device> kasaDevices = kasaService.listDevices();
        List<Device> tapoDevices = tapoService.listDevices();

        kasaDevices.forEach(device -> deviceMap.put(device.getId(), device));
        tapoDevices.forEach(device -> deviceMap.put(device.getId(), device));
    }

    public Device updateDevice(Device device) {
        deviceMap.put(device.getId(), device);
        return deviceMap.get(device.getId());
    }
}

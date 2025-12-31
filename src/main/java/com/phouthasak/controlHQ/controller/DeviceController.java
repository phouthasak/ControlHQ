package com.phouthasak.controlHQ.controller;

import com.phouthasak.controlHQ.model.dto.Device;
import com.phouthasak.controlHQ.service.DeviceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {
    @Autowired
    private DeviceManagementService deviceManagementService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity getDevices() {
        List<Device> devices = deviceManagementService.getDevices();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("devices", devices);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    @RequestMapping(value = "/{deviceId}", method = RequestMethod.GET)
    public ResponseEntity getDevice(@PathVariable("deviceId") String deviceId) {
        Device device = deviceManagementService.getDeviceInfo(deviceId);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("device", device);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }
}

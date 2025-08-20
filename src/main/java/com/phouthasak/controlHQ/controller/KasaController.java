package com.phouthasak.controlHQ.controller;

import com.phouthasak.controlHQ.model.dto.kasa.Device;
import com.phouthasak.controlHQ.model.dto.kasa.KasaDto;
import com.phouthasak.controlHQ.service.kasa.KasaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kasa/devices")
public class KasaController {
    @Autowired
    private KasaService kasaService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity getDevices() {
        List<Device> devices = kasaService.listDevices();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("devices", devices);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    @RequestMapping(value = "/{deviceId}", method = RequestMethod.GET)
    public ResponseEntity getDevice(@PathVariable("deviceId") String deviceId) {
        Device device = kasaService.getDevice(deviceId);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("device", device);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }


    @RequestMapping(value = "/{deviceId}", method = RequestMethod.POST)
    public ResponseEntity setRelayStat(@PathVariable("deviceId") String deviceId,
                                       @RequestBody KasaDto kasaDto) {
        Device device = kasaService.setRelayState(deviceId, kasaDto);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("device", device);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }
}

package com.phouthasak.controlHQ.controller;

import com.phouthasak.controlHQ.model.dto.KasaDto;
import com.phouthasak.controlHQ.model.dto.KasaSmartPlugSystemInfoResponse;
import com.phouthasak.controlHQ.service.kasa.KasaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/kasa")
public class KasaController {
    @Autowired
    private KasaService kasaService;

    @RequestMapping(value = "/system_info", method = RequestMethod.GET)
    public KasaSmartPlugSystemInfoResponse.DeviceInfo getSystemInfo() throws IOException {
        return kasaService.getDeviceInfo();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public KasaSmartPlugSystemInfoResponse.DeviceInfo setRelayStat(@RequestBody KasaDto kasaDto) throws IOException {
        return kasaService.setRelayState(kasaDto);
    }
}

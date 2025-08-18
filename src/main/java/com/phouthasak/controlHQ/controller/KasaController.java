package com.phouthasak.controlHQ.controller;

import com.phouthasak.controlHQ.domain.kasa.KasaPayloadBuilder;
import com.phouthasak.controlHQ.model.dto.KasaDto;
import com.phouthasak.controlHQ.service.kasa.KasaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/kasa")
public class KasaController {
    @Autowired
    private KasaService kasaService;

    @RequestMapping(value = "/system_info", method = RequestMethod.GET)
    public String getSystemInfo() throws IOException {
        String payload = KasaPayloadBuilder.getDeviceInfoPayload();
        String result = kasaService.sendCommand(payload);
        return result;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String setRelayStat(@RequestBody KasaDto kasaDto) throws IOException {
        String payload = KasaPayloadBuilder.getReplayStatePayload(Objects.nonNull(kasaDto) && kasaDto.getRelayState());
        String result = kasaService.sendCommand(payload);
        return result;
    }
}

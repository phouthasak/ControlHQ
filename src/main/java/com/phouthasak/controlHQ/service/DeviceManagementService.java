package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.model.dto.Device;
import com.phouthasak.controlHQ.service.kasa.KasaService;
import com.phouthasak.controlHQ.service.tapo.TapoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceManagementService {
    @Autowired
    private KasaService kasaService;

    @Autowired
    private TapoService tapoService;

    public List<Device> getDevices() {
        List<Device> allDevices = new ArrayList<>();
        List<Device> kasaDevices = kasaService.listDevices();
        List<Device> tapoDevices = tapoService.listDevices();

        allDevices.addAll(kasaDevices);
        allDevices.addAll(tapoDevices);
        return allDevices;
    }
}

package com.phouthasak.controlHQ.domain.tapo;

import com.phouthasak.controlHQ.domain.DeviceType;
import com.phouthasak.controlHQ.model.dto.Device;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TapoDeviceInfo {
    private String deviceName;
    private int imageWidth;
    private int imageHeight;
    private double frameRate;
    private String videoCodec;
    private String audioCodec;
    private int audioChannels;
    private Map<String, String> allMetaData;

    public Device toDevice() {
        Device device = Device.builder()
                .id(UUID.randomUUID().toString())
                .type(DeviceType.CAMERA)
                .externalId(this.deviceName)
                .model(null)
                .name(this.deviceName)
                .latitude(null)
                .longitude(null)
                .relayState(1)
                .errorCode(0)
                .build();
        return device;
    }
}

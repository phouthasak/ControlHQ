package com.phouthasak.controlHQ.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class KasaSmartPlugSystemInfoResponse implements Serializable {
    private DeviceInfo deviceInfo;

    @Getter
    @Setter
    @Builder
    public static class DeviceInfo {
        private Device device;
        private int errorCode;
    }

    @Getter
    @Setter
    @Builder
    public static class Device {
        private String deviceId;
        private String model;
        private String name;
        private Long latitude;
        private Long longitude;
        private int relayState;

        public boolean isOn() {
            return this.relayState == 1;
        }
    }
}

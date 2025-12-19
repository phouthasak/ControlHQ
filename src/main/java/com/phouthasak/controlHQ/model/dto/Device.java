package com.phouthasak.controlHQ.model.dto;

import com.phouthasak.controlHQ.domain.DeviceType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Device implements Serializable {
    private String id;
    private String externalId;
    private String model;
    private String name;
    private DeviceType type;
    private Long latitude;
    private Long longitude;
    private int relayState;
    private int errorCode;

    public boolean isOn() {
        return this.relayState == 1;
    }
}

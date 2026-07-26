package com.phouthasak.controlHQ.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phouthasak.controlHQ.domain.DeviceType;
import com.phouthasak.controlHQ.domain.DeviceStatus;
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
    private DeviceStatus status;

    @JsonIgnore
    public boolean isOn() {
        return this.relayState == 1;
    }
}

package com.phouthasak.controlHQ.domain;

import static com.phouthasak.controlHQ.domain.Company.KASA;
import static com.phouthasak.controlHQ.domain.Company.TAPO;

public enum DeviceType {
    PLUG(KASA),
    CAMERA(TAPO);

    private Company company;

    DeviceType(Company company) {
        this.company = company;
    }
}

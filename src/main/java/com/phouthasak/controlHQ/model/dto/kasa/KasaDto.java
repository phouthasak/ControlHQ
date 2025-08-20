package com.phouthasak.controlHQ.model.dto.kasa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KasaDto implements Serializable {
    private Boolean relayState;
}

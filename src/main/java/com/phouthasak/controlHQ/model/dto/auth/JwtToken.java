package com.phouthasak.controlHQ.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class JwtToken implements Serializable {
    private String token;
}

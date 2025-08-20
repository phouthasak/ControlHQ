package com.phouthasak.controlHQ.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DefaultException {
    private String errorCode;
    private String errorMessage;
}

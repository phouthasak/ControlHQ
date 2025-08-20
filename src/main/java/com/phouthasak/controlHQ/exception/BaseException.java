package com.phouthasak.controlHQ.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class BaseException extends RuntimeException implements Serializable {
    private int errorCode = -1;

    public BaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

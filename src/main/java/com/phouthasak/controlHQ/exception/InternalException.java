package com.phouthasak.controlHQ.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends BaseException {
    public InternalException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalException() {
        super("internal_error", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

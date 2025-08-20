package com.phouthasak.controlHQ.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidException extends BaseException {
    public InvalidException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }

    public InvalidException() {
        super("parameter_is_invalid", HttpStatus.BAD_REQUEST.value());
    }
}

package com.phouthasak.controlHQ.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.phouthasak.controlHQ.util.Constants.TRANSACTION_ID_KEY;

@ControllerAdvice
@Slf4j
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleInvalidException(BaseException ex, WebRequest request) {
        String tid = (String) request.getAttribute(TRANSACTION_ID_KEY, WebRequest.SCOPE_REQUEST);
        log.error("Exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tid", tid);
        body.put("timestamp", Instant.now());
        body.put("status", ex.getErrorCode());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getErrorCode()));
    }
}

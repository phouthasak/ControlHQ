package com.phouthasak.controlHQ.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class BaseResponse<T> implements Serializable {
    private boolean success;
    private Map<String, Object> error;
    private T data;

    public BaseResponse(T data, Map<String, Object> error, boolean success) {
        this.data = data;
        this.error = error;
        this.success = success;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(data, null, true);
    }
}

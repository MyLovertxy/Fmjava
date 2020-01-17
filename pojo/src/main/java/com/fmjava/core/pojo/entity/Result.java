package com.fmjava.core.pojo.entity;

import lombok.Data;

@Data
public class Result {
    private boolean result;
    private String message;

    public Result(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}

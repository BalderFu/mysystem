package com.mysystem.ai.model;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setData(data);
        result.setMessage("请求成功");
        return result;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMessage("请求成功");
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<T>();
        result.setCode(201);
        result.setMessage(message);
        return result;
    }
}

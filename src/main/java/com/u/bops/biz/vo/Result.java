package com.u.bops.biz.vo;

import com.u.bops.common.constants.ResultCode;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jinsong
 */
public class Result<T> {
    private int code;
    private String errorMessage;
    private T data;

    public Result() {
    }

    public Result(int code, String errorMessage, T data) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <R> Result<R> success(R data) {
        Result<R> r = new Result<>(ResultCode.OK, "", data);
        return r;
    }

    public static Result<Map<String, Object>> success(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        Result<Map<String, Object>> r = new Result<>(ResultCode.OK, "", data);
        return r;
    }

    public static Result<Map<String, Object>> success(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        Map<String, Object> data = new HashMap<>();
        data.put(k1, v1);
        data.put(k2, v2);
        data.put(k3, v3);
        Result<Map<String, Object>> r = new Result<>(ResultCode.OK, "", data);
        return r;
    }

    public static <R> Result<R> error(int code, String m) {
        Result<R> r = new Result<>(code, m, null);
        return r;
    }

    public static <F, T> Result<T> error(Result<F> s) {
        Result<T> r = new Result<>(s.getCode(), s.getErrorMessage(), null);
        return r;
    }

    public boolean isSuccess() {
        return code == ResultCode.OK;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

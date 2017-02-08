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
    private String type;
    private String seq;

    public static final String SEQ = "seq";
    public static final String TYPE = "type";

    public static final String TYPE_LOGIN = "1";
    public static final String TYPE_PUBLISH_MESSAGE = "1001";
    public static final String TYPE_RECEIVE_MESSAGE = "1002";
    public static final String TYPE_READ_MESSAGE = "1003";
    public static final String TYPE_PUBLISH_UNREAD_MESSAGE = "1004";
    public static final String TYPE_ADD_FRIEND = "2001";

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

    public static <R> Result<R> success(R data, String type) {
        Result<R> r = new Result<>(ResultCode.OK, "", data);
        r.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}

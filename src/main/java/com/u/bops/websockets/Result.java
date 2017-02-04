package com.u.bops.websockets;


import com.google.gson.Gson;

/**
 * User: jinsong
 */
public class Result<T> {
    private int code;
    private String errorMessage;
    private String msgType;
    private String seq;
    private T data;

    public Result() {
    }

    public Result(int code, String errorMessage, T data) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <R> Result<R> success(R data) {
        Result<R> r = new Result<R>(Message.OK_CODE, "", data);
        return r;
    }

    public static <R> Result<R> error(int code, String m) {
        Result<R> r = new Result<R>(code, m, null);
        return r;
    }

    public boolean isSuccess() {
        return code == Message.OK_CODE;
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

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

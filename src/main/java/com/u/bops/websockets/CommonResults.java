package com.u.bops.websockets;

/**
 * User: jinsong
 */
public class CommonResults {
    public static final String OK = Result.success(null).toString();
    public static final String PARAM_REQUIRED = Result.error(Message.MISSING_PARAM, "").toString();

    public static final String NO_HANDLER = Result.error(Message.NO_SUCH_HANDLER, "").toString();
    public static final String SYSTEM_EXCEPTION = Result.error(Message.EXCEPTION_ERROR, "").toString();
    public static final String AUTH_ERROR = Result.error(Message.AUTH_ERROR, "").toString();

}

package com.u.bops.websockets;

import com.u.bops.biz.vo.Result;

import static com.u.bops.biz.vo.Result.*;

/**
 * User: jinsong
 */
public class CommonResults {
    public static final String OK = success(null).toString();
    public static final String PARAM_REQUIRED = error(Message.MISSING_PARAM, "").toString();

    public static final String NO_HANDLER = error(Message.NO_SUCH_HANDLER, "").toString();
    public static final String SYSTEM_EXCEPTION = error(Message.EXCEPTION_ERROR, "").toString();
    public static final String AUTH_ERROR = error(Message.AUTH_ERROR, "").toString();

}

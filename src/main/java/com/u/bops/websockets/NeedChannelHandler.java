package com.u.bops.websockets;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;

/**
 * Created by 123 on 2014/11/21.
 */
public abstract class NeedChannelHandler extends WebsocketHandler {
    @Override
    public Result execute(JsonObject jsonObject, String defaultName, Result result) {
        return null;
    }

    public abstract Result execute(JsonObject jsonObject, String defaultName, Result result, Channel c);
}

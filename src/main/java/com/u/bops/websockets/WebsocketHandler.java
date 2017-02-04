package com.u.bops.websockets;

import com.google.gson.JsonObject;
import com.u.bops.websockets.Result;

public abstract class WebsocketHandler {

    public abstract Result execute(JsonObject jsonObject, String defaultName, Result result);

    public boolean requireLogin() {
        return true;
    }

    public boolean requireSign() {
        return true;
    }

}

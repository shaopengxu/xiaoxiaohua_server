package com.u.bops.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * User: jinsong
 */
public class JsonUtils {
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static <T> T fromJson(String json, Type classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static boolean isValidJson(String json) {
        try {
            fromJson(json, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

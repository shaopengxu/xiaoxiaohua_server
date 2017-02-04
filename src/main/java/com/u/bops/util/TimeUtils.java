package com.u.bops.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shpng on 2015/7/11.
 */
public class TimeUtils {

    public static boolean beforeToday(Date date) {
        Calendar current = Calendar.getInstance();
        Calendar before = Calendar.getInstance();
        before.setTime(date);
        return before.get(Calendar.YEAR) * 1000000 +
                before.get(Calendar.DAY_OF_YEAR) <
                current.get(Calendar.YEAR) * 1000000 +
                        current.get(Calendar.DAY_OF_YEAR);
    }

    public static void main(String []args){
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonParser.parse("abc"));
        System.out.println(jsonArray);
    }
}

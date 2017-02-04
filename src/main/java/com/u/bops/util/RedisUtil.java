package com.u.bops.util;

/**
 * User: jinsong
 */
public class RedisUtil {

    public static long rangeEnd(long offset, long rowCount) {
        long end = offset + rowCount - 1;
        return end > 0 ? end : 0;
    }
}

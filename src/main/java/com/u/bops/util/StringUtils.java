package com.u.bops.util;

import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Set;

public class StringUtils {

    public static final String alpha = "abcdefghijklmnopqrstuvwxyz1234567890";
    public static final String numbers = "0123456789";
    public static final String REDIS_NULL_VALUE = "REDIS_NULL_VALUE";

    public static String generateRandomName(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(alpha.charAt((int) (Math.random() * alpha.length())));
        }

        return sb.toString();
    }

    public static String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(numbers.charAt((int) (Math.random() * numbers.length())));
        }

        return sb.toString();
    }

    public static Set<String> getStringsUnique(Map<String, Object> m, String key, String delimiter) {
        String val = MapUtils.getString(m, key);
        if (val == null) {
            return null;
        }
        return Sets.newHashSet(org.apache.commons.lang.StringUtils.split(val, delimiter));
    }

    public static String[] getStrings(Map<String, Object> m, String key, String delimiter) {
        String val = MapUtils.getString(m, key);
        if (val == null) {
            return null;
        }
        return org.apache.commons.lang.StringUtils.split(val, delimiter);
    }

}

package com.u.bops.util;

import com.u.bops.biz.annotation.RedisField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created by xsp on 2014/11/27.
 */
public class ReflectUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(ReflectUtils.class);

    public static final String REDIS_NULL_VALUE = "REDIS_NULL_VALUE";

    public static <T> T getInstanceFromMap(Class<T> clazz, Map map) {
        T t = null;
        try {
            t = clazz.newInstance();
            for (Object fieldObj : map.keySet()) {
                String fieldKey = (String) fieldObj;
                Field field = getField(clazz, fieldKey);
                if (field != null) {
                    field.setAccessible(true);
                    field.set(t, string2Obj((String) map.get(fieldKey), field.getType()));
                }
            }

        } catch (Exception e) {
            logger.error("error, ", e);
        }
        return t;
    }

    public static Field getField(Class clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Objects.equals(field.getName(), fieldName)) {
                return field;
            }
            RedisField redisField = field.getAnnotation(RedisField.class);
            if (redisField != null) {
                if (Objects.equals(redisField.value(), fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static Object string2Obj(String s, Class<?> clazz) {
        if (s != null && s.equals(REDIS_NULL_VALUE)) {
            return null;
        }
        if (clazz == Integer.class || clazz == int.class) {
            if (s == null) {
                return 0;
            }
            return Integer.parseInt(s);
        } else if (clazz == Date.class) {
            if (s == null) {
                return null;
            }
            return new Date(Long.parseLong(s));
        } else if (clazz == Long.class || clazz == long.class) {
            if (s == null) {
                return 0;
            }
            return Long.parseLong(s);
        } else if (clazz == Byte.class || clazz == byte.class) {
            if (s == null) {
                return 0;
            }
            return Byte.parseByte(s);
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            if (s == null) {
                return false;
            }
            return Boolean.parseBoolean(s);
        } else if (clazz == Character.class || clazz == char.class) {
            if (s == null) {
                return 0;
            }
            return (char) Integer.parseInt(s);
        } else if (clazz == Double.class || clazz == double.class) {
            if (s == null) {
                return 0;
            }
            return Double.parseDouble(s);
        } else {
            return s;
        }
    }

    public static void main(String[] args) {

    }
}

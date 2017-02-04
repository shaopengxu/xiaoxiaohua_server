package com.u.bops.util;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * User: jinsong
 */
public class MapUtils {

    private static final Logger logger = LoggerFactory.getLogger(MapUtils.class);

    public static Date getDate(Map<String, Object> m, String key, String[] patterns) {
        Object val = m.get(key);
        if (val == null) {
            return null;
        }
        if (val instanceof Date) {
            return (Date) val;
        }
        if (val instanceof String) {
            try {
                return DateUtils.parseDate((String) val, patterns);
            } catch (ParseException e) {
                logger.error("parse date exception! key:" + key + ", val:" + val, e);
                return null;
            }
        }
        return null;
    }
}

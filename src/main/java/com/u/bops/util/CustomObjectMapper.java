package com.u.bops.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * User: jinsong
 */
public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}

package com.u.bops.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * User: jinsong
 */
public class BootstrapListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}

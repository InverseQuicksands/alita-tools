package com.alita.framework.job.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * i18n util
 */
public class I18nUtils {
    private static Logger logger = LoggerFactory.getLogger(I18nUtils.class);

    public static Properties prop = null;


    /**
     * get val of i18n key
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

}

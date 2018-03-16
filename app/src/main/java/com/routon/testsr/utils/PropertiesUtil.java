package com.routon.testsr.utils;

import java.util.Properties;

/**
 * Created by lch on 18-2-28.
 */

public class PropertiesUtil {
    public static String load(String key) {
        String value = null;
        Properties properties = new Properties();
        try {
            properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("assets/data.properties"));
            value = properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}

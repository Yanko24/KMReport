package com.yankee.kmreport.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class PropertiesUtil {
    private static Map<String, PropertiesUtil> propertiesMap;
    Properties properties;

    public static synchronized PropertiesUtil getInstance(String propertiesName) {
        PropertiesUtil instance = null;
        if (propertiesMap == null) {
            propertiesMap = new HashMap<>();
        }
        instance = propertiesMap.get(propertiesName);
        if (instance == null) {
            instance = new PropertiesUtil(propertiesName);
            propertiesMap.put(propertiesName, instance);
        }
        return instance;
    }

    private PropertiesUtil(String propertiesName) {
        InputStreamReader inputStreamReader =
                new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(propertiesName)));
        properties = new Properties();
        try {
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException("There's no resource file named [" + propertiesName + "]", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInteger(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public int getInteger(String key, int defaultValue) {
        return getProperty(key) == null ? defaultValue : Integer.parseInt(getProperty(key));
    }

    public long getLong(String key) {
        return Long.parseLong(getProperty(key));
    }

    public long getLong(String key, long defaultValue) {
        return getProperty(key) == null ? defaultValue : Long.parseLong(getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getProperty(key) == null ? defaultValue : Boolean.parseBoolean(getProperty(key));
    }

    public Properties getProperties() {
        return properties;
    }
}

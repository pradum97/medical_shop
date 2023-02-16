package com.techwhizer.medicalshop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public Properties getReadProp() {
        String path = "util/readQuery.properties";
        InputStream is = getClass().getResourceAsStream(path);
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Properties getUpdateProp() {
        String path = "util/updateQuery.properties";
        InputStream is = getClass().getResourceAsStream(path);
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Properties getDbDetails() {
        String path = "util/db.properties";
        InputStream is = getClass().getResourceAsStream(path);
        Properties properties = new Properties();

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return properties;
    }
}

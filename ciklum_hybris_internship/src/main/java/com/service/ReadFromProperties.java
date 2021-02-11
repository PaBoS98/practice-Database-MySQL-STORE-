package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ReadFromProperties {

    private static String URL;
    private static String USER_NAME;
    private static String PASSWORD;

    public ReadFromProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath() +
                "\\ciklum_hybris_internship\\src\\main\\resources\\local.properties"))) {
            props.load(in);
        }
        URL = props.getProperty("url");
        USER_NAME = props.getProperty("username");
        PASSWORD = props.getProperty("password");
    }

    public static String getURL() {
        return URL;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static void setURL(String URL) {
        ReadFromProperties.URL = URL;
    }

    public static void setUserName(String userName) {
        USER_NAME = userName;
    }

    public static void setPASSWORD(String PASSWORD) {
        ReadFromProperties.PASSWORD = PASSWORD;
    }
}

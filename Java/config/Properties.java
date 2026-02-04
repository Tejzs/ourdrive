package config;

import java.io.FileInputStream;

public class Properties {
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static String DB_PORT;
    private static String DB_HOST;
    private static String LOG_PATH;

    public static void loadConfigurations() {
        java.util.Properties props = new java.util.Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        DB_USERNAME = props.getProperty("db.user");
        DB_PASSWORD = props.getProperty("db.pass");
        DB_HOST = props.getProperty("db.host");
        DB_PORT = props.getProperty("db");
        LOG_PATH = props.getProperty("log/path");
    }


    public static String getDbUsername() {
        return DB_USERNAME;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static String getDbPort() {
        return DB_PORT;
    }

    public static String getDbHost() {
        return DB_HOST;
    }

    public static String getLogPath() {
        return LOG_PATH;
    }
}
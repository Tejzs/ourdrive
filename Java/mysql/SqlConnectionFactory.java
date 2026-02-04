package mysql;

import config.Properties;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlConnectionFactory {
    private static final String DB_URL = "jdbc:mysql://" + Properties.getDbHost() + ":" + Properties.getDbPort() + "/FileStorageDB";
    private static final String DB_USER = Properties.getDbUsername();
    private static final String DB_PASS = Properties.getDbPassword();

    private static Connection createConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        return conn;
    }

    public static Connection getConnection() throws Exception {
        return createConnection();
    }
}

package mysql;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlConnectionFactory {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/FileStorageDB";
    private static final String DB_USER = "root";
    private static final String DB_PASS = System.getenv("MYSQLDB_PASSWORD");;

    private static Connection createConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        return conn;
    }

    public static Connection getConnection() throws Exception {
        return createConnection();
    }
}

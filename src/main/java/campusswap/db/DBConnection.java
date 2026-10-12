package campusswap.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * One place that knows how to talk to the XAMPP MySQL server.
 * Every DAO calls DBConnection.getConnection() instead of
 * hard coding connection details everywhere.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/campusswap?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // default XAMPP MySQL password is blank

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC driver not found. In NetBeans: right-click project -> " +
                "Properties -> Libraries -> Add Library -> MySQL JDBC Driver.", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
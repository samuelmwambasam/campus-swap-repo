package campusswap.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Run this class by itself (right-click -> Run File) to confirm
 * Java can reach your XAMPP MySQL server before testing the rest
 * of the app. Delete it once you know the connection works.
 */
public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Connected successfully to: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("Connection FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

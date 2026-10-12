import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Simpledbtest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/campusswap";
        String username = "root";
        String password = ""; // default XAMPP password is blank

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
}
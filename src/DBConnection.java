import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database URL
    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_management"
        + "?useSSL=false&allowPublicKeyRetrieval=true";

    // Database credentials
    private static final String USER = "bank_user";
    private static final String PASSWORD = "bank@123";

    public static void main(String[] args) {

        try {
            // 1. Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Create connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 3. Check connection
            if (conn != null) {
                System.out.println("✅ Connected to MySQL Database successfully!");
            }

            // 4. Close connection
            conn.close();

        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("❌ Database connection failed");
            e.printStackTrace();
        }
    }
}
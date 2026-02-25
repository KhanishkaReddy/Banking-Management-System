import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertCustomer {

    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_management"
        + "?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "bank_user";
    private static final String PASSWORD = "bank@123";

    public static void main(String[] args) {

        String sql = "INSERT INTO customer " +
                     "(customer_id, name, address, phone, email, date_of_birth, pan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            // 1. Connect to DB
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 2. Prepare SQL
            PreparedStatement ps = conn.prepareStatement(sql);

            // 3. Set values (simulating bank employee input)
            ps.setInt(1, 103);
            ps.setString(2, "Rahul Verma");
            ps.setString(3, "Mumbai");
            ps.setString(4, "9988776655");
            ps.setString(5, "rahul@gmail.com");
            ps.setDate(6, java.sql.Date.valueOf("1999-08-15"));
            ps.setString(7, "ZXCVB1234Q");

            // 4. Execute
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Customer inserted successfully!");
            }

            // 5. Close
            ps.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("❌ Error inserting customer");
            e.printStackTrace();
        }
    }
}
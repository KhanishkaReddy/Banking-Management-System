import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertCustomer {

    public static void main(String[] args) {

        String sql = "INSERT INTO customer " +
                     "(name, address, phone, email, date_of_birth, pan) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            PreparedStatement ps = conn.prepareStatement(sql);

            // Simulating employee input
            ps.setString(1, "Rahul Verma");
            ps.setString(2, "Mumbai");
            ps.setString(3, "9988776655");
            ps.setString(4, "rahul@gmail.com");
            ps.setDate(5, java.sql.Date.valueOf("1999-08-15"));
            ps.setString(6, "ZXCVB1234Q");

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Customer inserted successfully!");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error inserting customer");
            e.printStackTrace();
        }
    }
}
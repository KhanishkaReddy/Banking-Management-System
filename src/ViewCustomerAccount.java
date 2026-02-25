import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewCustomerAccount {

    public static void main(String[] args) {

        String sql =
            "SELECT c.customer_id, c.name, c.phone, " +
            "a.account_no, a.account_type, a.balance " +
            "FROM customer c " +
            "JOIN account a ON c.customer_id = a.customer_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (conn == null) return;

            System.out.println("📋 Customer Account Details");
            System.out.println("--------------------------------------");

            while (rs.next()) {
                System.out.println(
                    "Customer ID : " + rs.getInt("customer_id") +
                    "\nName        : " + rs.getString("name") +
                    "\nPhone       : " + rs.getString("phone") +
                    "\nAccount No  : " + rs.getInt("account_no") +
                    "\nType        : " + rs.getString("account_type") +
                    "\nBalance     : ₹" + rs.getDouble("balance") +
                    "\n--------------------------------------"
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
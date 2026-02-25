import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewCustomerAccount {

    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_management"
        + "?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "bank_user";
    private static final String PASSWORD = "bank@123";

    public static void main(String[] args) {

        String sql =
            "SELECT c.customer_id, c.name, c.phone, " +
            "a.account_no, a.account_type, a.balance " +
            "FROM customer c " +
            "JOIN account a ON c.customer_id = a.customer_id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
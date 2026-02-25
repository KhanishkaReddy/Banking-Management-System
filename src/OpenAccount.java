import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class OpenAccount {

    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_management"
        + "?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "bank_user";
    private static final String PASSWORD = "bank@123";

    public static void main(String[] args) {

        try {
            // 1️⃣ Create SavingsAccount object (OOP validation happens here)
            SavingsAccount account =
                new SavingsAccount(6001, 15000.00, 103, 1);

            String sql =
                "INSERT INTO account " +
                "(account_no, account_type, balance, opened_date, status, customer_id, branch_id) " +
                "VALUES (?, 'Savings', ?, CURDATE(), 'Active', ?, ?)";

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql);

            // 2️⃣ Use object data
            ps.setInt(1, account.getAccountNo());
            ps.setDouble(2, account.getBalance());
            ps.setInt(3, account.getCustomerId());
            ps.setInt(4, account.getBranchId());

            ps.executeUpdate();

            System.out.println("✅ Savings Account opened successfully!");

        } catch (IllegalArgumentException e) {
            // Balance < 1000 case
            System.out.println("❌ " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
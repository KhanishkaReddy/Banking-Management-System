import java.sql.Connection;
import java.sql.PreparedStatement;

public class OpenAccount {

    public static void main(String[] args) {

        try {
            // 1️⃣ Create SavingsAccount object (OOP validation happens here)
            SavingsAccount account =
                new SavingsAccount(6001, 15000.00, 103, 1);

            String sql =
                "INSERT INTO account " +
                "(account_no, account_type, balance, opened_date, status, customer_id, branch_id) " +
                "VALUES (?, 'Savings', ?, CURDATE(), 'Active', ?, ?)";

            try (Connection conn = DBConnection.getConnection()) {

                if (conn == null) return;

                PreparedStatement ps = conn.prepareStatement(sql);

                // 2️⃣ Use object data
                ps.setInt(1, account.getAccountNo());
                ps.setDouble(2, account.getBalance());
                ps.setInt(3, account.getCustomerId());
                ps.setInt(4, account.getBranchId());

                ps.executeUpdate();

                System.out.println("✅ Savings Account opened successfully!");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
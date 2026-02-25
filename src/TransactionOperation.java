import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TransactionOperation {

    public static void main(String[] args) {

        int accountNo = 6001;
        double amount = 2000.00;
        String type = "Deposit";   // or "Withdrawal"

        String updateBalanceSQL =
            "UPDATE account SET balance = balance + ? WHERE account_no = ?";

        String insertTransactionSQL =
            "INSERT INTO transaction_details " +
            "(transaction_id, amount, transaction_type, transaction_date, description, account_no) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) return;

            conn.setAutoCommit(false);   // 🔥 Start transaction

            // 1️⃣ Update account balance
            PreparedStatement ps1 = conn.prepareStatement(updateBalanceSQL);
            ps1.setDouble(1, amount);
            ps1.setInt(2, accountNo);
            ps1.executeUpdate();

            // 2️⃣ Insert transaction record
            PreparedStatement ps2 = conn.prepareStatement(insertTransactionSQL);
            ps2.setInt(1, (int)(Math.random() * 100000));
            ps2.setDouble(2, amount);
            ps2.setString(3, type);
            ps2.setObject(4, LocalDateTime.now());
            ps2.setString(5, "Cash Deposit");
            ps2.setInt(6, accountNo);
            ps2.executeUpdate();

            conn.commit();   // ✅ Success

            System.out.println("✅ Transaction completed successfully!");

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();   // 🔥 Rollback on failure
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
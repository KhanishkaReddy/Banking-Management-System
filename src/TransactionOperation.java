import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TransactionOperation {

    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_management"
        + "?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "bank_user";
    private static final String PASSWORD = "bank@123";

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

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // 1. Update account balance
            PreparedStatement ps1 = conn.prepareStatement(updateBalanceSQL);
            ps1.setDouble(1, amount);
            ps1.setInt(2, accountNo);
            ps1.executeUpdate();

            // 2. Insert transaction record
            PreparedStatement ps2 = conn.prepareStatement(insertTransactionSQL);
            ps2.setInt(1, 10001); // transaction id
            ps2.setDouble(2, amount);
            ps2.setString(3, type);
            ps2.setObject(4, LocalDateTime.now());
            ps2.setString(5, "Cash Deposit");
            ps2.setInt(6, accountNo);
            ps2.executeUpdate();

            System.out.println("✅ Transaction completed successfully!");

            ps1.close();
            ps2.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
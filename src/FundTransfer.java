import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FundTransfer {

    public static void transfer() {

        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            if (conn == null) return;

            System.out.print("From Account No: ");
            int fromAcc = sc.nextInt();

            System.out.print("To Account No: ");
            int toAcc = sc.nextInt();

            System.out.print("Amount: ");
            double amount = sc.nextDouble();

            conn.setAutoCommit(false);   // Start transaction

            // 1️⃣ Check sender balance
            String checkSQL = "SELECT balance FROM account WHERE account_no = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSQL);
            psCheck.setInt(1, fromAcc);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Sender account not found");
                conn.rollback();
                return;
            }

            double currentBalance = rs.getDouble("balance");

            if (currentBalance < amount) {
                System.out.println("❌ Insufficient balance");
                conn.rollback();
                return;
            }

            // 2️⃣ Deduct from sender
            String deductSQL =
                "UPDATE account SET balance = balance - ? WHERE account_no = ?";
            PreparedStatement psDeduct = conn.prepareStatement(deductSQL);
            psDeduct.setDouble(1, amount);
            psDeduct.setInt(2, fromAcc);
            psDeduct.executeUpdate();

            // 3️⃣ Add to receiver
            String addSQL =
                "UPDATE account SET balance = balance + ? WHERE account_no = ?";
            PreparedStatement psAdd = conn.prepareStatement(addSQL);
            psAdd.setDouble(1, amount);
            psAdd.setInt(2, toAcc);
            psAdd.executeUpdate();

            // 4️⃣ Insert transaction record (Debit & Credit)
            String insertSQL =
                "INSERT INTO transaction_details " +
                "(transaction_id, amount, transaction_type, transaction_date, description, account_no) " +
                "VALUES (?, ?, ?, NOW(), ?, ?)";

            // Debit entry
            PreparedStatement psTrans1 = conn.prepareStatement(insertSQL);
            psTrans1.setInt(1, (int)(Math.random() * 100000));
            psTrans1.setDouble(2, amount);
            psTrans1.setString(3, "Transfer-Debit");
            psTrans1.setString(4, "Transfer to " + toAcc);
            psTrans1.setInt(5, fromAcc);
            psTrans1.executeUpdate();

            // Credit entry
            PreparedStatement psTrans2 = conn.prepareStatement(insertSQL);
            psTrans2.setInt(1, (int)(Math.random() * 100000));
            psTrans2.setDouble(2, amount);
            psTrans2.setString(3, "Transfer-Credit");
            psTrans2.setString(4, "Transfer from " + fromAcc);
            psTrans2.setInt(5, toAcc);
            psTrans2.executeUpdate();

            conn.commit();   // Success

            System.out.println("✅ Fund Transfer Successful!");

        } catch (Exception e) {

            try {
                if (conn != null) {
                    conn.rollback();   // Rollback on any failure
                }
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
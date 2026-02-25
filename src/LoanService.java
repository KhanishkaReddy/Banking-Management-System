import java.sql.*;
import java.util.Scanner;

public class LoanService {

    static Scanner sc = new Scanner(System.in);

    // ================= APPLY LOAN =================
    public static void applyLoan() {

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            System.out.print("Customer ID: ");
            int customerId = sc.nextInt();

            System.out.print("Loan Amount: ");
            double principal = sc.nextDouble();

            System.out.print("Annual Interest Rate (%): ");
            double annualRate = sc.nextDouble();

            System.out.print("Tenure (months): ");
            int tenure = sc.nextInt();

            double monthlyRate = annualRate / (12 * 100);
            double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
                    (Math.pow(1 + monthlyRate, tenure) - 1);

            String sql =
                "INSERT INTO loan (customer_id, loan_amount, interest_rate, tenure_months, emi, remaining_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setDouble(2, principal);
            ps.setDouble(3, annualRate);
            ps.setInt(4, tenure);
            ps.setDouble(5, emi);
            ps.setDouble(6, principal);

            ps.executeUpdate();

            System.out.println("\n✅ Loan Approved Successfully!");
            System.out.println("Monthly EMI: ₹" + Math.round(emi));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= VIEW LOAN =================
    public static void viewLoanDetails() {

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            System.out.print("Customer ID: ");
            int customerId = sc.nextInt();

            String sql = "SELECT * FROM loan WHERE customer_id = ? AND status='ACTIVE'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\nLoan Details:");
            System.out.println("-------------------------------------");

            while (rs.next()) {
                System.out.println("Loan ID        : " + rs.getInt("loan_id"));
                System.out.println("Loan Amount    : ₹" + rs.getDouble("loan_amount"));
                System.out.println("Interest Rate  : " + rs.getDouble("interest_rate") + "%");
                System.out.println("Tenure         : " + rs.getInt("tenure_months") + " months");
                System.out.println("EMI            : ₹" + rs.getDouble("emi"));
                System.out.println("Remaining Amt  : ₹" + rs.getDouble("remaining_amount"));
                System.out.println("Status         : " + rs.getString("status"));
                System.out.println("-------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PAY EMI (ATOMIC) =================
    public static void payEMI() {

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) return;

            conn.setAutoCommit(false);

            System.out.print("Loan ID: ");
            int loanId = sc.nextInt();

            System.out.print("Payment Amount: ");
            double amount = sc.nextDouble();

            String checkSQL =
                "SELECT remaining_amount FROM loan WHERE loan_id=? AND status='ACTIVE'";
            PreparedStatement psCheck = conn.prepareStatement(checkSQL);
            psCheck.setInt(1, loanId);

            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Loan not found or already closed");
                conn.rollback();
                return;
            }

            double remaining = rs.getDouble("remaining_amount");

            if (amount > remaining) {
                System.out.println("❌ Amount exceeds remaining loan");
                conn.rollback();
                return;
            }

            PreparedStatement psUpdate = conn.prepareStatement(
                "UPDATE loan SET remaining_amount = remaining_amount - ? WHERE loan_id=?"
            );
            psUpdate.setDouble(1, amount);
            psUpdate.setInt(2, loanId);
            psUpdate.executeUpdate();

            if (amount == remaining) {
                PreparedStatement psClose =
                    conn.prepareStatement("UPDATE loan SET status='CLOSED' WHERE loan_id=?");
                psClose.setInt(1, loanId);
                psClose.executeUpdate();
                System.out.println("🎉 Loan Fully Closed!");
            } else {
                System.out.println("✅ EMI Payment Successful");
            }

            conn.commit();

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
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

    // ================= CLOSE LOAN =================
    public static void closeLoan() {

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            System.out.print("Loan ID to Close: ");
            int loanId = sc.nextInt();

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE loan SET status='CLOSED', remaining_amount=0 WHERE loan_id=?"
            );
            ps.setInt(1, loanId);
            ps.executeUpdate();

            System.out.println("✅ Loan Closed Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= PREPAYMENT =================
    public static void prepayment() {

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            System.out.print("Loan ID: ");
            int loanId = sc.nextInt();

            System.out.print("Prepayment Amount: ");
            double extraAmount = sc.nextDouble();

            System.out.println("Choose Option:");
            System.out.println("1. Reduce EMI");
            System.out.println("2. Reduce Tenure");
            int option = sc.nextInt();

            String fetchSQL =
                "SELECT interest_rate, tenure_months, emi, remaining_amount " +
                "FROM loan WHERE loan_id=? AND status='ACTIVE'";

            PreparedStatement psFetch = conn.prepareStatement(fetchSQL);
            psFetch.setInt(1, loanId);

            ResultSet rs = psFetch.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Loan not found");
                return;
            }

            double remaining = rs.getDouble("remaining_amount");
            double rate = rs.getDouble("interest_rate");
            int tenure = rs.getInt("tenure_months");
            double emi = rs.getDouble("emi");

            if (extraAmount > remaining) {
                System.out.println("❌ Amount exceeds remaining");
                return;
            }

            double newPrincipal = remaining - extraAmount;
            double monthlyRate = rate / (12 * 100);

            if (option == 1) {

                double newEmi =
                    (newPrincipal * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
                    (Math.pow(1 + monthlyRate, tenure) - 1);

                PreparedStatement psUpdate = conn.prepareStatement(
                    "UPDATE loan SET remaining_amount=?, emi=? WHERE loan_id=?"
                );

                psUpdate.setDouble(1, newPrincipal);
                psUpdate.setDouble(2, newEmi);
                psUpdate.setInt(3, loanId);
                psUpdate.executeUpdate();

                System.out.println("✅ EMI Reduced Successfully!");
                System.out.println("New EMI: ₹" + Math.round(newEmi));

            } else {

                int newTenure =
                    (int)(Math.log(emi / (emi - newPrincipal * monthlyRate)) /
                    Math.log(1 + monthlyRate));

                PreparedStatement psUpdate = conn.prepareStatement(
                    "UPDATE loan SET remaining_amount=?, tenure_months=? WHERE loan_id=?"
                );

                psUpdate.setDouble(1, newPrincipal);
                psUpdate.setInt(2, newTenure);
                psUpdate.setInt(3, loanId);
                psUpdate.executeUpdate();

                System.out.println("✅ Tenure Reduced Successfully!");
                System.out.println("New Tenure: " + newTenure + " months");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
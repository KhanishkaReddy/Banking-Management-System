import java.sql.*;
import java.util.Scanner;

public class BankingApp {

    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_management?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "bank_user";
    private static final String PASSWORD = "bank@123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n🏦 BANKING MANAGEMENT SYSTEM (Savings Account)");
            System.out.println("1. Add Customer");
            System.out.println("2. Open Savings Account");
            System.out.println("3. Fund Transfer");
            System.out.println("4. Deposit Money");
            System.out.println("5. View Customer Accounts");
            System.out.println("6. Apply Loan");
            System.out.println("7. Pay EMI");
            System.out.println("8. View Loan Details");
            System.out.println("9. Close Loan");
            System.out.println("10. Loan Prepayment (Reduce EMI / Tenure)");
            System.out.println("11. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();   // 🔥 Clear buffer

            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> openSavingsAccount();
                case 3 -> FundTransfer.transfer();
                case 4 -> depositMoney();
                case 5 -> viewAccounts();
                case 6 -> LoanService.applyLoan();
                case 7 -> LoanService.payEMI();
                case 8 -> LoanService.viewLoanDetails();
                case 9 -> LoanService.closeLoan();
                case 10 -> LoanService.prepayment();
                case 11 -> {
                    System.out.println("Thank you!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    // 1. Add Customer
    static void addCustomer() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Address: ");
            String address = sc.nextLine();

            System.out.print("Phone: ");
            String phone = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("DOB (YYYY-MM-DD): ");
            String dob = sc.nextLine();

            System.out.print("PAN: ");
            String pan = sc.nextLine();

            String sql =
                "INSERT INTO customer (name, address, phone, email, date_of_birth, pan) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps =
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setDate(5, Date.valueOf(dob));
            ps.setString(6, pan);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("✅ Customer added successfully!");
                System.out.println("Generated Customer ID: " + generatedId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Open Savings Account
    static void openSavingsAccount() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.print("Account No: ");
            int accNo = sc.nextInt();

            System.out.print("Customer ID: ");
            int custId = sc.nextInt();

            System.out.print("Initial Balance (min ₹1000): ");
            double bal = sc.nextDouble();

            if (bal < 1000) {
                System.out.println("❌ Minimum balance for Savings Account is ₹1000");
                return;
            }

            String sql =
                "INSERT INTO account " +
                "(account_no, account_type, balance, opened_date, status, customer_id, branch_id) " +
                "VALUES (?, 'Savings', ?, CURDATE(), 'Active', ?, 1)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accNo);
            ps.setDouble(2, bal);
            ps.setInt(3, custId);

            ps.executeUpdate();
            System.out.println("✅ Savings Account opened successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Deposit Money
    static void depositMoney() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.print("Account No: ");
            int accNo = sc.nextInt();

            System.out.print("Deposit Amount: ");
            double amt = sc.nextDouble();

            // Check account existence
            String check = "SELECT balance FROM account WHERE account_no = ?";
            PreparedStatement psCheck = conn.prepareStatement(check);
            psCheck.setInt(1, accNo);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Account not found");
                return;
            }

            // Update balance
            String update =
                "UPDATE account SET balance = balance + ? WHERE account_no = ?";
            PreparedStatement ps1 = conn.prepareStatement(update);
            ps1.setDouble(1, amt);
            ps1.setInt(2, accNo);
            ps1.executeUpdate();

            // Insert transaction record
            String insert =
                "INSERT INTO transaction_details " +
                "(transaction_id, amount, transaction_type, transaction_date, description, account_no) " +
                "VALUES (?, ?, 'Deposit', NOW(), 'Cash Deposit', ?)";

            PreparedStatement ps2 = conn.prepareStatement(insert);
            ps2.setInt(1, (int)(Math.random() * 100000));
            ps2.setDouble(2, amt);
            ps2.setInt(3, accNo);
            ps2.executeUpdate();

            System.out.println("✅ Deposit successful");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. View Customer Accounts
    static void viewAccounts() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String sql =
                "SELECT c.customer_id, c.name, a.account_no, a.account_type, a.balance " +
                "FROM customer c JOIN account a ON c.customer_id = a.customer_id";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nCustID | Name | Account No | Type | Balance");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.println(
                rs.getInt("customer_id") + " | " +
                rs.getString("name") + " | " +
                rs.getInt("account_no") + " | " +
                rs.getString("account_type") + " | ₹" +
                rs.getDouble("balance")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
public class SavingsAccount {

    private int accountNo;
    private double balance;
    private int customerId;
    private int branchId;

    public static final double MIN_BALANCE = 1000.0;

    public SavingsAccount(int accountNo, double balance, int customerId, int branchId) {
        if (balance < MIN_BALANCE) {
            throw new IllegalArgumentException(
                "Minimum balance for Savings Account is ₹1000"
            );
        }
        this.accountNo = accountNo;
        this.balance = balance;
        this.customerId = customerId;
        this.branchId = branchId;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public double getBalance() {
        return balance;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getBranchId() {
        return branchId;
    }
}
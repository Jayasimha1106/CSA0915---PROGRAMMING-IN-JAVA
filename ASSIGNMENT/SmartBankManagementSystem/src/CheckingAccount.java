/**
 * CheckingAccount represents a transactional account with overdraft facilities.
 * Demonstrates:
 * 1. Inheritance: Extends Account
 * 2. Method Overriding: Overrides calculateInterest(), displayAccountDetails(), and withdraw()
 * 3. Runtime Polymorphism
 */
public class CheckingAccount extends Account {
    private static final long serialVersionUID = 1L;

    private double overdraftLimit;  // e.g., $1000.00
    private double transactionFee;   // e.g., $1.50 per overdraft usage

    // Default Constructor
    public CheckingAccount() {
        super();
        this.accountType = "Checking";
        this.overdraftLimit = 1000.0;
        this.transactionFee = 1.50;
    }

    // Parameterized Constructor
    public CheckingAccount(String accountNumber, String customerId, double balance, double overdraftLimit, double transactionFee) {
        super(accountNumber, customerId, "Checking", balance);
        this.overdraftLimit = overdraftLimit;
        this.transactionFee = transactionFee;
    }

    public CheckingAccount(String accountNumber, String customerId, double balance) {
        this(accountNumber, customerId, balance, 1000.0, 1.50);
    }

    // Getters and Setters
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    /**
     * Overridden method: Checking accounts do not earn substantial interest (0.0).
     */
    @Override
    public double calculateInterest() {
        return 0.0;
    }

    /**
     * Overridden withdraw method permitting overdraft within limit with fee.
     */
    @Override
    public synchronized void withdraw(double amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: " + amount);
        }
        double totalRequired = amount;
        if (amount > balance) {
            totalRequired += transactionFee; // Overdraft penalty fee
        }

        if ((balance - totalRequired) < -overdraftLimit) {
            throw new InsufficientBalanceException(String.format(
                    "Withdrawal failed for Checking Account %s. Exceeds overdraft limit ($%.2f). Current Balance: $%.2f, Required: $%.2f",
                    accountNumber, overdraftLimit, balance, totalRequired));
        }
        this.balance -= totalRequired;
    }

    /**
     * Overridden displayAccountDetails() method.
     */
    @Override
    public String displayAccountDetails() {
        return String.format("Checking Account #%s | Customer: %s | Balance: $%.2f | Overdraft Limit: $%.2f | Fee: $%.2f",
                accountNumber, customerId, balance, overdraftLimit, transactionFee);
    }
}

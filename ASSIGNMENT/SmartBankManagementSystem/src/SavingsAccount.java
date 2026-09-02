/**
 * SavingsAccount represents an interest-bearing personal deposit account.
 * Demonstrates:
 * 1. Inheritance: Extends Account
 * 2. Method Overriding: Overrides calculateInterest(), displayAccountDetails(), and withdraw()
 * 3. Runtime Polymorphism
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    private double interestRate;   // e.g., 4.5% annual
    private double minimumBalance; // e.g., $500.00

    // Default Constructor
    public SavingsAccount() {
        super();
        this.accountType = "Savings";
        this.interestRate = 4.5;
        this.minimumBalance = 500.0;
    }

    // Parameterized Constructor
    public SavingsAccount(String accountNumber, String customerId, double balance, double interestRate, double minimumBalance) {
        super(accountNumber, customerId, "Savings", balance);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
    }

    public SavingsAccount(String accountNumber, String customerId, double balance) {
        this(accountNumber, customerId, balance, 4.5, 500.0);
    }

    // Getters and Setters
    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    /**
     * Overridden method to calculate annual interest earned.
     * Demonstrates Method Overriding & Runtime Polymorphism.
     */
    @Override
    public double calculateInterest() {
        return Math.round((balance * (interestRate / 100.0)) * 100.0) / 100.0;
    }

    /**
     * Overridden withdraw method enforcing minimum balance.
     */
    @Override
    public synchronized void withdraw(double amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: " + amount);
        }
        if ((this.balance - amount) < minimumBalance) {
            throw new InsufficientBalanceException(String.format(
                    "Withdrawal failed for Savings Account %s. Minimum balance of $%.2f must be maintained. Current: $%.2f, Requested: $%.2f",
                    accountNumber, minimumBalance, balance, amount));
        }
        this.balance -= amount;
    }

    /**
     * Overridden displayAccountDetails() method.
     */
    @Override
    public String displayAccountDetails() {
        return String.format("Savings Account #%s | Customer: %s | Balance: $%.2f | Int Rate: %.2f%% | Min Bal: $%.2f | Annual Int: $%.2f",
                accountNumber, customerId, balance, interestRate, minimumBalance, calculateInterest());
    }
}

import java.io.Serializable;

/**
 * Abstract base class for all bank accounts.
 * Demonstrates:
 * 1. Abstraction: Abstract class defining common structure and abstract methods.
 * 2. Interface Implementation: Implements BankOperations interface.
 * 3. Encapsulation: Protected/private fields with public accessors.
 * 4. Multithreading & Synchronization: Synchronized methods and dead-lock safe transfers.
 * 5. Method Overloading: deposit(double) and deposit(double, String).
 */
public abstract class Account implements BankOperations, Serializable {
    private static final long serialVersionUID = 1L;

    protected String accountNumber;
    protected String customerId;
    protected String accountType; // "Savings" or "Checking"
    protected double balance;

    // Default Constructor
    public Account() {
        this.accountNumber = "";
        this.customerId = "";
        this.accountType = "Savings";
        this.balance = 0.0;
    }

    // Parameterized Constructor
    public Account(String accountNumber, String customerId, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Synchronized deposit operation.
     * Demonstrates Thread-Safety / Synchronization.
     */
    @Override
    public synchronized void deposit(double amount) throws BankException {
        deposit(amount, "Standard Deposit");
    }

    /**
     * Overloaded synchronized deposit with custom description.
     * Demonstrates Method Overloading & Synchronization.
     */
    @Override
    public synchronized void deposit(double amount, String description) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive. Provided: " + amount);
        }
        this.balance += amount;
    }

    /**
     * Synchronized withdrawal operation.
     * Overridden in subclasses for specific limits and rules.
     */
    @Override
    public synchronized void withdraw(double amount) throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive. Provided: " + amount);
        }
        if (this.balance < amount) {
            throw new InsufficientBalanceException(String.format(
                    "Insufficient balance in account %s. Current balance: $%.2f, Requested: $%.2f",
                    accountNumber, balance, amount));
        }
        this.balance -= amount;
    }

    /**
     * Thread-safe fund transfer between two accounts.
     * Prevents race conditions and deadlocks by locking accounts in a deterministic order.
     */
    @Override
    public void transfer(Account targetAccount, double amount) throws BankException {
        if (targetAccount == null) {
            throw new InvalidAccountException("Target account cannot be null.");
        }
        if (this.accountNumber.equalsIgnoreCase(targetAccount.getAccountNumber())) {
            throw new InvalidTransactionException("Cannot transfer funds to the same account.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive. Provided: " + amount);
        }

        // Deadlock-free ordering: lock the account with the smaller account number first
        Account firstLock = this.accountNumber.compareTo(targetAccount.getAccountNumber()) < 0 ? this : targetAccount;
        Account secondLock = this.accountNumber.compareTo(targetAccount.getAccountNumber()) < 0 ? targetAccount : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                // Perform withdrawal from sender
                this.withdraw(amount);
                // Perform deposit into receiver
                targetAccount.deposit(amount, "Transfer from " + this.accountNumber);
            }
        }
    }

    @Override
    public synchronized double checkBalance() {
        return this.balance;
    }

    // Abstract methods to be overridden by subclasses (Polymorphism)
    public abstract double calculateInterest();
    public abstract String displayAccountDetails();

    @Override
    public String toString() {
        return String.format("[%s] Acc: %s | Cust: %s | Bal: $%.2f", accountType, accountNumber, customerId, balance);
    }
}

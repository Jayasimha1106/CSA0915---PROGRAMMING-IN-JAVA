import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Transaction entity representing an individual account operation.
 * Demonstrates Encapsulation, Constructors, and Serialization.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String accountNumber;
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    private double amount;
    private String transactionDate;
    private String description;

    // Default Constructor
    public Transaction() {
        this.transactionId = "";
        this.accountNumber = "";
        this.transactionType = "";
        this.amount = 0.0;
        this.transactionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.description = "";
    }

    // Parameterized Constructor
    public Transaction(String transactionId, String accountNumber, String transactionType, double amount, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.description = description;
    }

    // Full Parameterized Constructor
    public Transaction(String transactionId, String accountNumber, String transactionType, double amount, String transactionDate, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Acc: %s | Type: %-12s | Amt: $%.2f | Desc: %s",
                transactionDate, transactionId, accountNumber, transactionType, amount, description);
    }
}

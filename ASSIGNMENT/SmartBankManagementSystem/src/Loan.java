import java.io.Serializable;

/**
 * Loan entity representing credit/loan facilities extended to customers.
 * Demonstrates Encapsulation, Business Logic, and Serialization.
 */
public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String loanId;
    private String customerId;
    private String loanType; // Home, Personal, Education, Business
    private double amount;
    private double interestRate; // Annual % (e.g., 7.5)
    private int duration; // in months
    private String status; // PENDING, APPROVED, REJECTED, CLOSED

    // Default Constructor
    public Loan() {
        this.loanId = "";
        this.customerId = "";
        this.loanType = "Personal";
        this.amount = 0.0;
        this.interestRate = 8.5;
        this.duration = 12;
        this.status = "PENDING";
    }

    // Parameterized Constructor
    public Loan(String loanId, String customerId, String loanType, double amount, double interestRate, int duration, String status) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.loanType = loanType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.duration = duration;
        this.status = status;
    }

    // Getters and Setters
    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Calculates Equated Monthly Installment (EMI).
     * EMI = [P x R x (1+R)^N]/[(1+R)^N-1]
     */
    public double calculateEmi() {
        if (amount <= 0 || duration <= 0) return 0.0;
        double monthlyRate = (interestRate / 100.0) / 12.0;
        if (monthlyRate == 0) return amount / duration;
        double emi = (amount * monthlyRate * Math.pow(1 + monthlyRate, duration)) /
                (Math.pow(1 + monthlyRate, duration) - 1);
        return Math.round(emi * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("Loan ID: %s | Cust: %s | Type: %s | Amt: $%.2f | Rate: %.2f%% | Dur: %dm | Status: %s | EMI: $%.2f",
                loanId, customerId, loanType, amount, interestRate, duration, status, calculateEmi());
    }
}

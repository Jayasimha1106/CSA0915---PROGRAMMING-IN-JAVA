/**
 * Worker thread for executing concurrent withdrawal operations.
 * Demonstrates:
 * 1. Multithreading: Extends Thread
 * 2. Thread Priority: Runs at Thread.NORM_PRIORITY (Priority 5)
 * 3. Exception Handling during concurrent execution
 * 4. Synchronization: Interacts safely with synchronized Account methods.
 */
public class WithdrawalThread extends Thread {
    private Bank bank;
    private String accountNumber;
    private double amount;
    private String description;
    private String logResult;
    private boolean success;

    public WithdrawalThread(Bank bank, String accountNumber, double amount, String description) {
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
        this.success = false;
        // Set Thread Priority
        this.setPriority(Thread.NORM_PRIORITY);
        this.setName("WithdrawThread-" + accountNumber);
    }

    @Override
    public void run() {
        try {
            Account account = bank.getAccount(accountNumber);
            double beforeBalance;
            synchronized (account) {
                beforeBalance = account.getBalance();
            }

            // Perform synchronized withdrawal through Bank
            bank.withdraw(accountNumber, amount, description);

            double afterBalance;
            synchronized (account) {
                afterBalance = account.getBalance();
            }

            this.success = true;
            this.logResult = String.format("[SUCCESS] [%s] Withdrew $%.2f from Acc %s. Before: $%.2f | After: $%.2f",
                    getName(), amount, accountNumber, beforeBalance, afterBalance);
        } catch (Exception e) {
            this.success = false;
            this.logResult = String.format("[FAILED] [%s] Withdrawal of $%.2f on Acc %s failed: %s",
                    getName(), amount, accountNumber, e.getMessage());
        }
    }

    public String getLogResult() {
        return logResult;
    }

    public boolean isSuccess() {
        return success;
    }
}

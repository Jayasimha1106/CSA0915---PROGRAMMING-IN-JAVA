/**
 * Worker thread for executing high-priority fund transfer operations.
 * Demonstrates:
 * 1. Multithreading: Extends Thread
 * 2. Thread Priority: Runs at Thread.MAX_PRIORITY (Priority 10) for critical transfers.
 * 3. Multi-Resource Synchronization: Atomic two-account locking preventing race conditions and deadlocks.
 */
public class TransferThread extends Thread {
    private Bank bank;
    private String senderAccount;
    private String receiverAccount;
    private double amount;
    private String description;
    private String logResult;
    private boolean success;

    public TransferThread(Bank bank, String senderAccount, String receiverAccount, double amount, String description) {
        this.bank = bank;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.description = description;
        this.success = false;
        // Critical transaction given highest priority
        this.setPriority(Thread.MAX_PRIORITY);
        this.setName("TransferThread-" + senderAccount + "->" + receiverAccount);
    }

    @Override
    public void run() {
        try {
            Account sender = bank.getAccount(senderAccount);
            Account receiver = bank.getAccount(receiverAccount);
            double senderBefore, receiverBefore;
            synchronized (sender) {
                senderBefore = sender.getBalance();
            }
            synchronized (receiver) {
                receiverBefore = receiver.getBalance();
            }

            // Perform synchronized transfer
            bank.transfer(senderAccount, receiverAccount, amount, description);

            double senderAfter, receiverAfter;
            synchronized (sender) {
                senderAfter = sender.getBalance();
            }
            synchronized (receiver) {
                receiverAfter = receiver.getBalance();
            }

            this.success = true;
            this.logResult = String.format("[SUCCESS] [%s] Transferred $%.2f from %s ($%.2f -> $%.2f) to %s ($%.2f -> $%.2f)",
                    getName(), amount, senderAccount, senderBefore, senderAfter, receiverAccount, receiverBefore, receiverAfter);
        } catch (Exception e) {
            this.success = false;
            this.logResult = String.format("[FAILED] [%s] Transfer of $%.2f from %s to %s failed: %s",
                    getName(), amount, senderAccount, receiverAccount, e.getMessage());
        }
    }

    public String getLogResult() {
        return logResult;
    }

    public boolean isSuccess() {
        return success;
    }
}

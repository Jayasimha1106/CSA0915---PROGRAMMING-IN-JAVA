/**
 * Interface defining standard banking operations.
 * Demonstrates the Interface concept and Method Contracts in OOP.
 */
public interface BankOperations {
    /**
     * Deposits the specified amount into the account.
     * @param amount the amount to deposit
     * @throws BankException if amount is invalid or account inactive
     */
    void deposit(double amount) throws BankException;

    /**
     * Overloaded method: Deposits amount with a specific transaction description.
     * Demonstrates Method Overloading in OOP.
     * @param amount the amount to deposit
     * @param description specific description for the transaction
     * @throws BankException if amount is invalid
     */
    void deposit(double amount, String description) throws BankException;

    /**
     * Withdraws the specified amount from the account.
     * @param amount the amount to withdraw
     * @throws BankException if insufficient balance, overdraft exceeded, or amount invalid
     */
    void withdraw(double amount) throws BankException;

    /**
     * Transfers funds to another account safely.
     * @param targetAccount destination account
     * @param amount amount to transfer
     * @throws BankException if validation fails, balances insufficient, or deadlock risk
     */
    void transfer(Account targetAccount, double amount) throws BankException;

    /**
     * Returns the current balance.
     * @return current account balance
     */
    double checkBalance();
}

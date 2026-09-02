/**
 * Exception thrown when an account balance is insufficient for withdrawal/transfer.
 * Demonstrates Custom Exception Handling and Business Logic Enforcement.
 */
public class InsufficientBalanceException extends BankException {
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}

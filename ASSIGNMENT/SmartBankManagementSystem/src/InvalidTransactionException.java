/**
 * Exception thrown when a transaction is invalid (e.g., same sender and receiver).
 * Demonstrates Custom Exception Handling.
 */
public class InvalidTransactionException extends BankException {
    private static final long serialVersionUID = 1L;

    public InvalidTransactionException(String message) {
        super(message);
    }
}

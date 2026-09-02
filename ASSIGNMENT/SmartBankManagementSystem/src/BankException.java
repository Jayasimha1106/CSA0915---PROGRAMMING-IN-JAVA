/**
 * Base custom exception for all banking operations.
 * Demonstrates Custom Exception Handling in Java.
 */
public class BankException extends Exception {
    private static final long serialVersionUID = 1L;

    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}

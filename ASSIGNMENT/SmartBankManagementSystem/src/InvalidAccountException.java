/**
 * Exception thrown when an account is not found or invalid.
 * Demonstrates Custom Exception Handling and Inheritance.
 */
public class InvalidAccountException extends BankException {
    private static final long serialVersionUID = 1L;

    public InvalidAccountException(String message) {
        super(message);
    }
}

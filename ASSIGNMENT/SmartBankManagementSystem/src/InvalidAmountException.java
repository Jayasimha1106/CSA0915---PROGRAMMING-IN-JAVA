/**
 * Exception thrown when an invalid (negative or zero or non-numeric) amount is specified.
 * Demonstrates Custom Exception Handling and Input Validation.
 */
public class InvalidAmountException extends BankException {
    private static final long serialVersionUID = 1L;

    public InvalidAmountException(String message) {
        super(message);
    }
}

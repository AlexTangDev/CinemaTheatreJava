package nl.inholland.endassignment.endproject.exceptions;

public class AccountLockedException extends Exception {
    public AccountLockedException(String message) {
        super(message);
    }
}
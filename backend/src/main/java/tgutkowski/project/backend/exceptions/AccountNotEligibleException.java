package tgutkowski.project.backend.exceptions;

public class AccountNotEligibleException extends RuntimeException {
    public AccountNotEligibleException(String message) {
        super(message);
    }
}

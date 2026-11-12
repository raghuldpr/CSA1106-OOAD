package edu.simats.shas;

/** Raised when a business rule (BR1-BR7) refuses an operation. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}

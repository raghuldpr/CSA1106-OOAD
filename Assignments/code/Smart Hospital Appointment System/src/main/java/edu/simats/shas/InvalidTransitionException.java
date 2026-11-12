package edu.simats.shas;

/** Raised when an operation is not allowed in the current state (never silently ignored). */
public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(String message) { super(message); }
}

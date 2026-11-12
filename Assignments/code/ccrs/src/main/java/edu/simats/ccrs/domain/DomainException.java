package edu.simats.ccrs.domain;

/** Thrown when a business rule of the domain is violated. */
public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
}

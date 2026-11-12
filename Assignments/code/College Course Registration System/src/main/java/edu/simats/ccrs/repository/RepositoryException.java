package edu.simats.ccrs.repository;

/** Persistence failure translated into a technology-neutral exception. */
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message, Throwable cause) { super(message, cause); }
    public RepositoryException(String message) { super(message); }
}

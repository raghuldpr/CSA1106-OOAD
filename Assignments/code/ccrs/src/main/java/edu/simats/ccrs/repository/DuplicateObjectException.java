package edu.simats.ccrs.repository;

/** A create() was attempted for an identifier that already exists (not a connectivity problem). */
public class DuplicateObjectException extends RepositoryException {
    public DuplicateObjectException(String message) { super(message); }
}

package com.example.usermanagement.exception;

/**
 * Thrown when attempting to create or update a user with an email that is
 * already registered.
 *
 * <p>
 * Mapped to HTTP 409 by the global exception handler.
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Builds an exception describing the conflicting email.
     *
     * @param email email that is already registered
     */
    public DuplicateEmailException(final String email) {
        super("Email already registered: " + email);
    }
}

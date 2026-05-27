package com.example.usermanagement.exception;

/**
 * Thrown when a user is requested but does not exist.
 *
 * <p>
 * Mapped to HTTP 404 by the global exception handler.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Builds an exception describing a missing user id.
     *
     * @param id id that was not found
     */
    public UserNotFoundException(final Long id) {
        super("User not found with id: " + id);
    }
}

package com.example.usermanagement.service;

import com.example.usermanagement.model.User;

import java.util.List;

/**
 * Business operations for managing {@link User} records.
 */
public interface UserService {

    /**
     * Creates a new user.
     *
     * @param user user to persist; must not be {@code null}
     * @return the persisted user with generated id
     */
    User create(User user);

    /**
     * Returns all users.
     *
     * @return list of users (never {@code null}; may be empty)
     */
    List<User> findAll();

    /**
     * Finds a user by id.
     *
     * @param id user id; must not be {@code null}
     * @return the matching user
     */
    User findById(Long id);

    /**
     * Updates an existing user.
     *
     * @param id      id of the user to update; must not be {@code null}
     * @param updates fields to apply; must not be {@code null}
     * @return the updated user
     */
    User update(Long id, User updates);

    /**
     * Deletes the user with the given id.
     *
     * @param id user id; must not be {@code null}
     */
    void delete(Long id);
}

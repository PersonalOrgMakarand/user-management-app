package com.example.usermanagement.repository;

import com.example.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User}.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * boolean taken = userRepository.existsByEmail("alice@example.com");
 * </pre>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Looks up a user by email.
     *
     * @param email email address to search for; must not be {@code null}
     * @return {@link Optional} containing the user if found, otherwise empty
     */
    Optional<User> findByEmail(String email);

    /**
     * Looks up a user by name (case-insensitive).
     *
     * @param name name to search for
     * @return {@link Optional} containing the first matching user if found
     */
    Optional<User> findFirstByNameIgnoreCase(String name);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email email address to check; must not be {@code null}
     * @return {@code true} if a user exists with that email
     */
    boolean existsByEmail(String email);
}

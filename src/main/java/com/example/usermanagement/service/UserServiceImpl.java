package com.example.usermanagement.service;

import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Default {@link UserService} implementation backed by {@link UserRepository}.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * User created = userService.create(new User("Alice", "alice@example.com", "secret1", "ADMIN"));
 * </pre>
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the service.
     *
     * @param userRepository repository dependency; must not be {@code null}
     */
    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder must not be null");
    }

    /**
     * Creates a new user after validating email uniqueness.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * User created = userService.create(new User("Bob", "bob@x.com", "secret1", "USER"));
     * </pre>
     *
     * @param user user to persist; must not be {@code null}
     * @return the persisted user with generated id
     * @throws IllegalArgumentException if {@code user} is {@code null}
     * @throws DuplicateEmailException  if the email is already registered
     */
    @Override
    public User create(final User user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }

        // Force a new entity to be inserted, even if caller supplied an id.
        user.setId(null);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Returns all users.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * List&lt;User&gt; all = userService.findAll();
     * </pre>
     *
     * @return all users (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Finds a user by id.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * User u = userService.findById(1L);
     * </pre>
     *
     * @param id user id; must not be {@code null}
     * @return the matching user
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws UserNotFoundException    if no user has that id
     */
    @Override
    @Transactional(readOnly = true)
    public User findById(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Updates the name, email, password, and role of an existing user.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * User updated = userService.update(1L, new User("Alice B", "alice@x.com", "secret1", "USER"));
     * </pre>
     *
     * @param id      id of the user to update; must not be {@code null}
     * @param updates field values to apply; must not be {@code null}
     * @return the updated user
     * @throws IllegalArgumentException if any argument is {@code null}
     * @throws UserNotFoundException    if no user has that id
     * @throws DuplicateEmailException  if the new email is taken by another user
     */
    @Override
    public User update(final Long id, final User updates) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (updates == null) {
            throw new IllegalArgumentException("updates must not be null");
        }

        final User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        final String newEmail = updates.getEmail();
        if (newEmail != null && !newEmail.equals(existing.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        existing.setName(updates.getName());
        existing.setEmail(updates.getEmail());
        existing.setPassword(passwordEncoder.encode(updates.getPassword()));
        existing.setRole(updates.getRole());

        return userRepository.save(existing);
    }

    /**
     * Deletes the user with the given id.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * userService.delete(1L);
     * </pre>
     *
     * @param id user id; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws UserNotFoundException    if no user has that id
     */
    @Override
    public void delete(final Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }
}

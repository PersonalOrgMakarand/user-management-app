package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * REST controller exposing CRUD endpoints for {@link User}.
 *
 * <p>
 * Base path: {@code /users}.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;

    /**
     * Constructs the controller.
     *
     * @param userService service dependency; must not be {@code null}
     */
    public UserController(final UserService userService) {
        this.userService = Objects.requireNonNull(userService, "userService must not be null");
    }

    /**
     * Creates a new user.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * POST /users
     * { "name": "Alice", "email": "a@x.com", "password": "secret1", "role": "ADMIN" }
     * </pre>
     *
     * @param user user to create (validated)
     * @return 201 Created with body and {@code Location} header
     */
    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<User> create(@Valid @RequestBody final User user) {
        final User created = userService.create(user);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * Lists all users.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * GET / users
     * </pre>
     *
     * @return 200 with the list of users
     */
    @GetMapping
    @Operation(summary = "List all users")
    @ApiResponse(responseCode = "200", description = "Users returned")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Returns a single user by id.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * GET / users / 1
     * </pre>
     *
     * @param id user id
     * @return 200 with the user, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Updates an existing user.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * PUT /users/1
     * { "name": "Alice B", "email": "a@x.com", "password": "secret1", "role": "USER" }
     * </pre>
     *
     * @param id      id of the user to update
     * @param updates updated user fields (validated)
     * @return 200 with the updated user
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<User> update(@PathVariable final Long id,
            @Valid @RequestBody final User updates) {
        return ResponseEntity.ok(userService.update(id, updates));
    }

    /**
     * Deletes a user by id.
     *
     * <p>
     * Example usage:
     * 
     * <pre>
     * DELETE / users / 1
     * </pre>
     *
     * @param id user id
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

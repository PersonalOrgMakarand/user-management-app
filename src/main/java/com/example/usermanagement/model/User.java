package com.example.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * JPA entity representing an application user.
 *
 * <p>
 * Mapped to table {@code users}. Email is unique. Password is accepted on
 * write but never serialised in API responses (write-only JSON property).
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * User u = new User("Alice", "alice@example.com", "secret1", "ADMIN");
 * </pre>
 */
@Entity
@Table(name = "users")
@Schema(description = "Application user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated user id", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 100, message = "name length must be between 2 and 100")
    @Column(nullable = false, length = 100)
    @Schema(description = "Full name", example = "Alice Smith", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid address")
    @Column(nullable = false, unique = true, length = 150)
    @Schema(description = "Unique email address", example = "alice@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password must be between 6 and 100 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    @Schema(description = "Password (write-only, never returned)", example = "s3cret!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "role is required")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    @Schema(description = "Role assigned to the user", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;

    /** Default constructor required by JPA. */
    public User() {
        // no-op
    }

    /**
     * Convenience constructor.
     *
     * @param name     user's full name
     * @param email    unique email address
     * @param password user's password (plain text in this demo)
     * @param role     role identifier (e.g. {@code ADMIN}, {@code USER})
     */
    public User(final String name, final String email, final String password, final String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role='" + role + "'}";
    }
}

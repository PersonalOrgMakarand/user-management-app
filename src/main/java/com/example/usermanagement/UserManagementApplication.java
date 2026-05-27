package com.example.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the User Management Spring Boot application.
 *
 * <p>
 * Boots an embedded Tomcat server and exposes REST APIs for managing users
 * backed by an in-memory H2 database.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * java -jar app.jar
 * </pre>
 */
@SpringBootApplication
public class UserManagementApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments forwarded to Spring Boot
     */
    public static void main(final String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }
}

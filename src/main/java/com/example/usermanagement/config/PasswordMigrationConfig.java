package com.example.usermanagement.config;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordMigrationConfig {

    @Bean
    public ApplicationRunner passwordMigrationRunner(final UserRepository userRepository,
            final PasswordEncoder passwordEncoder) {
        return args -> {
            for (User user : userRepository.findAll()) {
                if (!isBcryptHash(user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                }
            }
        };
    }

    private boolean isBcryptHash(final String value) {
        return value != null && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }
}

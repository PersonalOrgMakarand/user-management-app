package com.example.usermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that verifies the Spring application context loads cleanly.
 */
@SpringBootTest
class UserManagementApplicationTests {

    /**
     * Loads the Spring context to ensure bean wiring is valid.
     */
    @Test
    void contextLoads() {
        // intentionally empty: failure to load context will fail the test
    }
}

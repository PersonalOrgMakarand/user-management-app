package com.example.usermanagement.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final long blockWindowSeconds;

    public LoginAttemptService(
            @Value("${auth.rate-limit.max-attempts:5}") final int maxAttempts,
            @Value("${auth.rate-limit.window-seconds:900}") final long blockWindowSeconds) {
        this.maxAttempts = maxAttempts;
        this.blockWindowSeconds = blockWindowSeconds;
    }

    public boolean isBlocked(final String key) {
        final AttemptState state = attempts.get(key);
        if (state == null) {
            return false;
        }
        if (isWindowExpired(state)) {
            attempts.remove(key);
            return false;
        }
        return state.failures >= maxAttempts;
    }

    public void recordFailure(final String key) {
        attempts.compute(key, (k, state) -> {
            if (state == null || isWindowExpired(state)) {
                return new AttemptState(1, Instant.now());
            }
            state.failures++;
            return state;
        });
    }

    public void reset(final String key) {
        attempts.remove(key);
    }

    private boolean isWindowExpired(final AttemptState state) {
        return state.firstAttempt.plusSeconds(blockWindowSeconds).isBefore(Instant.now());
    }

    private static final class AttemptState {
        private int failures;
        private final Instant firstAttempt;

        private AttemptState(final int failures, final Instant firstAttempt) {
            this.failures = failures;
            this.firstAttempt = firstAttempt;
        }
    }
}

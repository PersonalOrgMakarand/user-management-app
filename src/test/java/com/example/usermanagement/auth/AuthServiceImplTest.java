package com.example.usermanagement.auth;

import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.LoginResponse;
import com.example.usermanagement.dto.LogoutResponse;
import com.example.usermanagement.exception.InvalidCredentialsException;
import com.example.usermanagement.exception.TooManyLoginAttemptsException;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtService;
import com.example.usermanagement.security.LoginAttemptService;
import com.example.usermanagement.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("alice@example.com");
        loginRequest.setPassword("secret1");
    }

    @Test
    void loginWithValidCredentialsReturnsTokenResponse() {
        final User user = new User("Alice", "alice@example.com", "$2a$hash", "ADMIN");
        user.setId(1L);

        when(loginAttemptService.isBlocked(any())).thenReturn(false);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret1", "$2a$hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        final LoginResponse response = authService.login(loginRequest, "127.0.0.1");

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresInSeconds()).isEqualTo(3600);
        verify(loginAttemptService).reset("127.0.0.1:alice@example.com");
    }

    @Test
    void loginWithInvalidCredentialsThrowsExceptionAndTracksFailure() {
        final User user = new User("Alice", "alice@example.com", "$2a$hash", "ADMIN");

        when(loginAttemptService.isBlocked(any())).thenReturn(false);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret1", "$2a$hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest, "127.0.0.1"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(loginAttemptService).recordFailure("127.0.0.1:alice@example.com");
    }

    @Test
    void loginWhenBlockedThrowsTooManyAttempts() {
        when(loginAttemptService.isBlocked(any())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(loginRequest, "127.0.0.1"))
                .isInstanceOf(TooManyLoginAttemptsException.class);

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void logoutBlacklistsValidToken() {
        when(jwtService.isTokenValid("jwt-token")).thenReturn(true);
        when(jwtService.extractExpiration("jwt-token")).thenReturn(Date.from(Instant.now().plusSeconds(3600)));

        final LogoutResponse response = authService.logout("jwt-token");

        assertThat(response.message()).isEqualTo("Logged out successfully");
        verify(tokenBlacklistService).blacklist(eq("jwt-token"), any(Instant.class));
    }

    @Test
    void logoutWithoutTokenStillSucceeds() {
        final LogoutResponse response = authService.logout(null);

        assertThat(response.message()).isEqualTo("Logged out successfully");
        verify(tokenBlacklistService, never()).blacklist(any(), any());
    }
}

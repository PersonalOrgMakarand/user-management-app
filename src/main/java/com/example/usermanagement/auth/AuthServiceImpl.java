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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginAttemptService loginAttemptService;

    public AuthServiceImpl(final UserRepository userRepository,
            final JwtService jwtService,
            final PasswordEncoder passwordEncoder,
            final TokenBlacklistService tokenBlacklistService,
            final LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public LoginResponse login(final LoginRequest request, final String clientIp) {
        final String normalizedIdentifier = request.getUsernameOrEmail().trim();
        final String rateLimitKey = (clientIp + ":" + normalizedIdentifier).toLowerCase();

        if (loginAttemptService.isBlocked(rateLimitKey)) {
            LOGGER.warn("Blocked login attempt for identifier={} from ip={}", normalizedIdentifier, clientIp);
            throw new TooManyLoginAttemptsException();
        }

        final Optional<User> userByEmail = userRepository.findByEmail(normalizedIdentifier);
        final Optional<User> userByName = userRepository.findFirstByNameIgnoreCase(normalizedIdentifier);
        final User user = userByEmail.or(() -> userByName).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailure(rateLimitKey);
            LOGGER.warn("Failed login attempt for identifier={} from ip={}", normalizedIdentifier, clientIp);
            throw new InvalidCredentialsException();
        }

        loginAttemptService.reset(rateLimitKey);

        final String token = jwtService.generateToken(user);
        LOGGER.info("Successful login for user={} from ip={}", user.getEmail(), clientIp);

        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs() / 1000);
    }

    @Override
    public LogoutResponse logout(final String token) {
        if (token != null && jwtService.isTokenValid(token)) {
            final Instant expiresAt = jwtService.extractExpiration(token).toInstant();
            tokenBlacklistService.blacklist(token, expiresAt);
        }

        SecurityContextHolder.clearContext();
        return new LogoutResponse("Logged out successfully");
    }
}

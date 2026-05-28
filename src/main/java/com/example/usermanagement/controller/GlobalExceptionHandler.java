package com.example.usermanagement.controller;

import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.InvalidCredentialsException;
import com.example.usermanagement.exception.TooManyLoginAttemptsException;
import com.example.usermanagement.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralised REST exception handling. Translates known exceptions into a
 * structured {@link ErrorResponse} body with an appropriate HTTP status.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Structured error payload returned to clients.
     *
     * @param timestamp moment the error was produced
     * @param status    HTTP status code
     * @param error     short reason phrase
     * @param message   human-readable message
     * @param path      request URI
     * @param details   optional field-level details (validation errors)
     */
    public record ErrorResponse(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<String> details) {
    }

    /**
     * Handles missing-user lookups.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return 404 Not Found with structured body
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final UserNotFoundException ex,
            final HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /**
     * Handles email conflicts on create/update.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return 409 Conflict with structured body
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(final DuplicateEmailException ex,
            final HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(final InvalidCredentialsException ex,
            final HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
    }

    @ExceptionHandler(TooManyLoginAttemptsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyLoginAttempts(final TooManyLoginAttemptsException ex,
            final HttpServletRequest request) {
        return build(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request, null);
    }

    /**
     * Handles bean-validation failures on {@code @Valid @RequestBody} arguments.
     *
     * @param ex      the validation exception
     * @param request the current HTTP request
     * @return 400 Bad Request with per-field error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {
        final List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    /**
     * Handles invalid arguments propagated from the service layer.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(final IllegalArgumentException ex,
            final HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    /**
     * Fallback for any unhandled exception.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(final Exception ex,
            final HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request, null);
    }

    private ResponseEntity<ErrorResponse> build(final HttpStatus status,
            final String message,
            final HttpServletRequest request,
            final List<String> details) {
        final ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details);

        return ResponseEntity.status(status).body(body);
    }
}

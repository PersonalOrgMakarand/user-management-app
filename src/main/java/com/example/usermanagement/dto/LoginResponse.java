package com.example.usermanagement.dto;

public record LoginResponse(String token, String tokenType, long expiresInSeconds) {
}

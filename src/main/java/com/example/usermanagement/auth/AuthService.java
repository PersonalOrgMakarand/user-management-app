package com.example.usermanagement.auth;

import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.LoginResponse;
import com.example.usermanagement.dto.LogoutResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request, String clientIp);

    LogoutResponse logout(String token);
}

package com.bistu.clubsystembackend.service;

import com.bistu.clubsystembackend.entity.request.LoginRequest;
import com.bistu.clubsystembackend.entity.request.RefreshTokenRequest;
import com.bistu.clubsystembackend.entity.request.RegisterRequest;
import com.bistu.clubsystembackend.entity.request.SsoLoginRequest;
import com.bistu.clubsystembackend.entity.response.AuthMeResponseData;
import com.bistu.clubsystembackend.entity.response.LoginResponseData;
import com.bistu.clubsystembackend.entity.response.RegisterResponseData;

public interface AuthService {
    LoginResponseData login(LoginRequest request);

    RegisterResponseData register(RegisterRequest request);

    LoginResponseData ssoLogin(SsoLoginRequest request);

    AuthMeResponseData me(Long userId);

    LoginResponseData refreshToken(RefreshTokenRequest request);

    void logout(Long userId);
}

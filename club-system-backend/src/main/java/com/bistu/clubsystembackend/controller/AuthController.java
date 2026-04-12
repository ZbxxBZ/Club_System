package com.bistu.clubsystembackend.controller;

import com.bistu.clubsystembackend.entity.request.LoginRequest;
import com.bistu.clubsystembackend.entity.request.RefreshTokenRequest;
import com.bistu.clubsystembackend.entity.request.RegisterRequest;
import com.bistu.clubsystembackend.entity.request.SsoLoginRequest;
import com.bistu.clubsystembackend.entity.response.AuthMeResponseData;
import com.bistu.clubsystembackend.entity.response.LoginResponseData;
import com.bistu.clubsystembackend.entity.response.RegisterResponseData;
import com.bistu.clubsystembackend.service.AuthService;
import com.bistu.clubsystembackend.util.AccessChecker;
import com.bistu.clubsystembackend.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseData> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("登录成功", authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponseData> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @PostMapping("/sso/login")
    public ApiResponse<LoginResponseData> ssoLogin(@Valid @RequestBody SsoLoginRequest request) {
        return ApiResponse.success("登录成功", authService.ssoLogin(request));
    }

    @GetMapping("/me")
    public ApiResponse<AuthMeResponseData> me() {
        Long userId = AccessChecker.requireLogin().getUserId();
        return ApiResponse.success(authService.me(userId));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponseData> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        Long userId = AccessChecker.requireLogin().getUserId();
        authService.logout(userId);
        return ApiResponse.success("退出成功", null);
    }
}

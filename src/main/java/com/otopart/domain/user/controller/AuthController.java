package com.otopart.domain.user.controller;

import com.otopart.domain.user.dto.*;
import com.otopart.domain.user.service.AuthService;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Kayıt ve giriş işlemleri")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Müşteri kaydı")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Kayıt başarılı!", authService.register(request)));
    }

    @PostMapping("/register/mechanic")
    @Operation(summary = "Usta (B2B) kaydı")
    public ResponseEntity<ApiResponse<AuthResponse>> registerMechanic(
            @Valid @RequestBody MechanicRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Usta kaydı başarılı!", authService.registerMechanic(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "Giriş yap")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Token yenile")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request.getRefreshToken())));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "E-posta doğrula")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("E-posta doğrulandı!"));
    }
}
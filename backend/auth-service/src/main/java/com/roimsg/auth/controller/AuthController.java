package com.roimsg.auth.controller;

import com.roimsg.auth.dto.AuthResponse;
import com.roimsg.auth.dto.LoginRequest;
import com.roimsg.auth.dto.RefreshTokenRequest;
import com.roimsg.auth.dto.UserInfo;
import com.roimsg.auth.service.AuthService;
import com.roimsg.auth.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 인증 컨트롤러
 * 로그인, 로그아웃, 토큰 갱신 등의 인증 관련 API를 제공합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "사용자 인증 관련 API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Google OAuth 로그인
     */
    @PostMapping("/login/google")
    @Operation(summary = "Google OAuth 로그인", description = "Google 계정으로 로그인합니다.")
    public ResponseEntity<AuthResponse> loginWithGoogle(
            @Parameter(description = "Google OAuth 코드") @RequestParam String code,
            @Parameter(description = "테넌트 ID") @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantId,
            HttpServletRequest request) {
        
        try {
            AuthResponse response = authService.loginWithGoogle(code, tenantId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 로그인 (이메일/비밀번호)
     */
    @PostMapping("/login")
    @Operation(summary = "이메일 로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            @Parameter(description = "테넌트 ID") @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantId,
            HttpServletRequest request) {
        
        try {
            AuthResponse response = authService.login(loginRequest, tenantId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 액세스 토큰을 갱신합니다.")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        
        try {
            AuthResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃시킵니다.")
    public ResponseEntity<Void> logout(
            @Parameter(description = "액세스 토큰") @RequestHeader("Authorization") String token) {
        
        try {
            String accessToken = token.substring(7); // "Bearer " 제거
            authService.logout(accessToken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ResponseEntity<UserInfo> getCurrentUser(
            @Parameter(description = "액세스 토큰") @RequestHeader("Authorization") String token) {
        
        try {
            String accessToken = token.substring(7); // "Bearer " 제거
            UserInfo userInfo = authService.getCurrentUser(accessToken);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 토큰 유효성 검증
     */
    @GetMapping("/validate")
    @Operation(summary = "토큰 검증", description = "토큰의 유효성을 검증합니다.")
    public ResponseEntity<Boolean> validateToken(
            @Parameter(description = "액세스 토큰") @RequestHeader("Authorization") String token) {
        
        try {
            String accessToken = token.substring(7); // "Bearer " 제거
            boolean isValid = jwtUtil.validateToken(accessToken);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Google OAuth 리다이렉트 URI
     */
    @GetMapping("/oauth/google")
    @Operation(summary = "Google OAuth 리다이렉트", description = "Google OAuth 인증 후 리다이렉트됩니다.")
    public ResponseEntity<String> googleOAuthRedirect(
            @Parameter(description = "인증 코드") @RequestParam(required = false) String code,
            @Parameter(description = "에러") @RequestParam(required = false) String error,
            @Parameter(description = "테넌트 ID") @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantId) {
        
        if (error != null) {
            return ResponseEntity.badRequest().body("인증에 실패했습니다: " + error);
        }
        
        if (code == null) {
            return ResponseEntity.badRequest().body("인증 코드가 없습니다.");
        }
        
        // 프론트엔드로 리다이렉트 (실제 구현에서는 프론트엔드 URL로 리다이렉트)
        return ResponseEntity.ok("인증이 완료되었습니다. 프론트엔드에서 토큰을 처리해주세요.");
    }
}

package com.roimsg.auth.service;

import com.roimsg.auth.dto.AuthResponse;
import com.roimsg.auth.dto.LoginRequest;
import com.roimsg.auth.dto.UserInfo;
import com.roimsg.auth.entity.Tenant;
import com.roimsg.auth.entity.User;
import com.roimsg.auth.repository.TenantRepository;
import com.roimsg.auth.repository.UserRepository;
import com.roimsg.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 인증 서비스
 * 사용자 인증, 토큰 관리, Google OAuth 연동 기능을 제공합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${multitenancy.default-tenant-id}")
    private String defaultTenantId;

    /**
     * Google OAuth 로그인
     */
    public AuthResponse loginWithGoogle(String code, UUID tenantId, HttpServletRequest request) {
        try {
            // 테넌트 ID 설정
            UUID finalTenantId = tenantId != null ? tenantId : UUID.fromString(defaultTenantId);

            // 1) 코드 교환하여 액세스 토큰 획득
            String accessTokenFromGoogle = exchangeCodeForAccessToken(code);

            // 2) 사용자 정보 조회
            GoogleUser googleUser = fetchGoogleUser(accessTokenFromGoogle);

            // 3) 사용자 조회 또는 생성
            User user = findOrCreateUser(finalTenantId, googleUser.id, googleUser.email, googleUser.name, googleUser.picture);

            // 4) 로그인 시간 업데이트
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // 5) JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getName(), user.getTenantId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getTenantId());

            // 6) 사용자 정보 DTO 생성
            UserInfo userInfo = createUserInfo(user);

            return new AuthResponse(accessToken, refreshToken, 86400L, userInfo);

        } catch (Exception e) {
            throw new RuntimeException("Google 로그인에 실패했습니다.", e);
        }
    }

    private record GoogleUser(String id, String email, String name, String picture) {}

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    private String exchangeCodeForAccessToken(String code) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            String form = "code=" + java.net.URLEncoder.encode(code, java.nio.charset.StandardCharsets.UTF_8)
                    + "&client_id=" + java.net.URLEncoder.encode(googleClientId, java.nio.charset.StandardCharsets.UTF_8)
                    + "&client_secret=" + java.net.URLEncoder.encode(googleClientSecret, java.nio.charset.StandardCharsets.UTF_8)
                    + "&redirect_uri=" + java.net.URLEncoder.encode(googleRedirectUri, java.nio.charset.StandardCharsets.UTF_8)
                    + "&grant_type=authorization_code";

            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(googleTokenUri))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(form))
                    .build();

            java.net.http.HttpResponse<String> resp = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                com.fasterxml.jackson.databind.JsonNode json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(resp.body());
                return json.get("access_token").asText();
            }
            throw new RuntimeException("토큰 교환 실패: status=" + resp.statusCode());
        } catch (Exception e) {
            throw new RuntimeException("Google 토큰 교환 실패", e);
        }
    }

    private GoogleUser fetchGoogleUser(String googleAccessToken) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(googleUserInfoUri))
                    .header("Authorization", "Bearer " + googleAccessToken)
                    .GET()
                    .build();
            java.net.http.HttpResponse<String> resp = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode json = mapper.readTree(resp.body());
                String id = json.path("id").asText();
                String email = json.path("email").asText();
                String name = json.path("name").asText();
                String picture = json.path("picture").asText();
                return new GoogleUser(id, email, name, picture);
            }
            throw new RuntimeException("사용자 정보 조회 실패: status=" + resp.statusCode());
        } catch (Exception e) {
            throw new RuntimeException("Google 사용자 정보 조회 실패", e);
        }
    }

    /**
     * 이메일/비밀번호 로그인
     */
    public AuthResponse login(LoginRequest loginRequest, UUID tenantId, HttpServletRequest request) {
        try {
            // 테넌트 ID 설정
            UUID finalTenantId = tenantId != null ? tenantId : UUID.fromString(defaultTenantId);

            // 사용자 조회
            Optional<User> userOpt = userRepository.findByTenantIdAndEmail(finalTenantId, loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }

            User user = userOpt.get();
            if (!user.getIsActive()) {
                throw new RuntimeException("비활성화된 사용자입니다.");
            }

            // 비밀번호 검증 (실제 구현에서는 암호화된 비밀번호 비교)
            // 여기서는 간단히 처리
            if (!"password123".equals(loginRequest.getPassword())) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }

            // 로그인 시간 업데이트
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getName(), user.getTenantId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getTenantId());

            // 사용자 정보 DTO 생성
            UserInfo userInfo = createUserInfo(user);

            return new AuthResponse(accessToken, refreshToken, 86400L, userInfo);

        } catch (Exception e) {
            throw new RuntimeException("로그인에 실패했습니다.", e);
        }
    }

    /**
     * 토큰 갱신
     */
    public AuthResponse refreshToken(String refreshToken) {
        try {
            // 리프레시 토큰 검증
            if (!jwtUtil.isRefreshToken(refreshToken) || !jwtUtil.validateToken(refreshToken)) {
                throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
            }

            // 사용자 ID 추출
            UUID userId = jwtUtil.getUserIdFromToken(refreshToken);
            UUID tenantId = jwtUtil.getTenantIdFromToken(refreshToken);

            // 사용자 조회
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }

            User user = userOpt.get();

            // 새로운 토큰 생성
            String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getName(), user.getTenantId());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getTenantId());

            // 사용자 정보 DTO 생성
            UserInfo userInfo = createUserInfo(user);

            return new AuthResponse(newAccessToken, newRefreshToken, 86400L, userInfo);

        } catch (Exception e) {
            throw new RuntimeException("토큰 갱신에 실패했습니다.", e);
        }
    }

    /**
     * 로그아웃
     */
    public void logout(String accessToken) {
        try {
            // 토큰 검증
            if (!jwtUtil.validateToken(accessToken)) {
                throw new RuntimeException("유효하지 않은 토큰입니다.");
            }

            // 실제 구현에서는 Redis에 블랙리스트 토큰 저장
            // 여기서는 간단히 처리
            System.out.println("사용자 로그아웃: " + jwtUtil.getUserIdFromToken(accessToken));

        } catch (Exception e) {
            throw new RuntimeException("로그아웃에 실패했습니다.", e);
        }
    }

    /**
     * 현재 사용자 정보 조회
     */
    public UserInfo getCurrentUser(String accessToken) {
        try {
            // 토큰 검증
            if (!jwtUtil.validateToken(accessToken)) {
                throw new RuntimeException("유효하지 않은 토큰입니다.");
            }

            // 사용자 ID 추출
            UUID userId = jwtUtil.getUserIdFromToken(accessToken);

            // 사용자 조회
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }

            User user = userOpt.get();
            return createUserInfo(user);

        } catch (Exception e) {
            throw new RuntimeException("사용자 정보 조회에 실패했습니다.", e);
        }
    }

    /**
     * 사용자 조회 또는 생성
     */
    private User findOrCreateUser(UUID tenantId, String googleId, String email, String name, String profileImageUrl) {
        // 기존 사용자 조회
        Optional<User> existingUser = userRepository.findByTenantIdAndGoogleId(tenantId, googleId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 새 사용자 생성
        User newUser = new User(tenantId, googleId, email, name);
        newUser.setProfileImageUrl(profileImageUrl);
        newUser.setIsActive(true);

        return userRepository.save(newUser);
    }

    /**
     * 사용자 정보 DTO 생성
     */
    private UserInfo createUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setTenantId(user.getTenantId());
        userInfo.setGoogleId(user.getGoogleId());
        userInfo.setEmail(user.getEmail());
        userInfo.setName(user.getName());
        userInfo.setProfileImageUrl(user.getProfileImageUrl());
        userInfo.setPhoneNumber(user.getPhoneNumber());
        userInfo.setAddress(user.getAddress());
        userInfo.setCustomProfileImageUrl(user.getCustomProfileImageUrl());
        userInfo.setIsActive(user.getIsActive());
        userInfo.setLastLoginAt(user.getLastLoginAt());
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setUpdatedAt(user.getUpdatedAt());

        return userInfo;
    }
}

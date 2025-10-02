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
            // Google OAuth 코드로 사용자 정보 조회 (실제 구현에서는 Google API 호출)
            // 여기서는 샘플 데이터를 사용
            String googleId = "google_" + System.currentTimeMillis();
            String email = "user@example.com";
            String name = "Google 사용자";
            String profileImageUrl = "https://via.placeholder.com/150";

            // 테넌트 ID 설정
            UUID finalTenantId = tenantId != null ? tenantId : UUID.fromString(defaultTenantId);

            // 사용자 조회 또는 생성
            User user = findOrCreateUser(finalTenantId, googleId, email, name, profileImageUrl);

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
            throw new RuntimeException("Google 로그인에 실패했습니다.", e);
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

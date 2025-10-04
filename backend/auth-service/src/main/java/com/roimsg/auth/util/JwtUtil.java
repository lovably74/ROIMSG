package com.roimsg.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JWT 유틸리티 클래스
 * JWT 토큰 생성, 검증, 파싱 기능을 제공합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${jwt.signup-expiration}")
    private long signupExpiration;

    /**
     * JWT 시크릿 키 생성
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 액세스 토큰 생성
     */
    public String generateAccessToken(UUID userId, String email, String name, UUID tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", name);
        claims.put("tenantId", tenantId.toString());
        claims.put("type", "access");
        
        return createToken(claims, userId.toString(), jwtExpiration);
    }

    /**
     * 리프레시 토큰 생성
     */
    public String generateRefreshToken(UUID userId, UUID tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());
        claims.put("type", "refresh");
        
        return createToken(claims, userId.toString(), refreshExpiration);
    }

    /**
     * Google 회원가입 토큰 생성 (단기 유효)
     */
    public String generateSignupToken(UUID tenantId, String googleId, String email, String name, String picture) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId.toString());
        claims.put("type", "signup");
        claims.put("googleId", googleId);
        claims.put("email", email);
        claims.put("name", name);
        claims.put("picture", picture);
        // subject는 googleId로 설정
        return createToken(claims, googleId, signupExpiration);
    }

    /**
     * JWT 토큰 생성
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public UUID getUserIdFromToken(String token) {
        String subject = getClaimFromToken(token, Claims::getSubject);
        return UUID.fromString(subject);
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("email", String.class));
    }

    /**
     * 토큰에서 이름 추출
     */
    public String getNameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("name", String.class));
    }

    /**
     * 토큰에서 테넌트 ID 추출
     */
    public UUID getTenantIdFromToken(String token) {
        String tenantId = getClaimFromToken(token, claims -> claims.get("tenantId", String.class));
        return tenantId != null ? UUID.fromString(tenantId) : null;
    }

    /**
     * 토큰에서 토큰 타입 추출
     */
    public String getTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }

    /**
     * 토큰에서 만료일 추출
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임 추출
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 토큰 유효성 검증
     */
    public Boolean validateToken(String token, UUID userId) {
        try {
            final UUID tokenUserId = getUserIdFromToken(token);
            return (tokenUserId.equals(userId) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰 유효성 검증 (사용자 ID 없이)
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 액세스 토큰인지 확인
     */
    public Boolean isAccessToken(String token) {
        String type = getTokenTypeFromToken(token);
        return "access".equals(type);
    }

    /**
     * 리프레시 토큰인지 확인
     */
    public Boolean isRefreshToken(String token) {
        String type = getTokenTypeFromToken(token);
        return "refresh".equals(type);
    }

    /**
     * Google 회원가입 토큰인지 확인
     */
    public Boolean isSignupToken(String token) {
        String type = getTokenTypeFromToken(token);
        return "signup".equals(type);
    }

    /**
     * 토큰에서 남은 시간(초) 계산
     */
    public Long getRemainingTimeInSeconds(String token) {
        Date expiration = getExpirationDateFromToken(token);
        long remainingTime = expiration.getTime() - System.currentTimeMillis();
        return remainingTime > 0 ? remainingTime / 1000 : 0;
    }
}

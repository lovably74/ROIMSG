package com.roimsg.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 인증 필터
 * API Gateway에서 JWT 토큰을 검증하고 사용자 정보를 추출합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${multitenancy.tenant-header}")
    private String tenantHeader;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 인증이 필요하지 않은 경로 체크
            if (isPublicPath(request.getPath().value())) {
                return chain.filter(exchange);
            }

            // Authorization 헤더에서 JWT 토큰 추출
            String token = extractTokenFromRequest(request);
            
            if (!StringUtils.hasText(token)) {
                return handleUnauthorized(response, "토큰이 없습니다.");
            }

            try {
                // JWT 토큰 검증
                Claims claims = validateToken(token);
                
                // 사용자 정보를 헤더에 추가
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Email", claims.get("email", String.class))
                    .header("X-User-Name", claims.get("name", String.class))
                    .header(tenantHeader, getTenantId(claims))
                    .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
                
            } catch (Exception e) {
                return handleUnauthorized(response, "유효하지 않은 토큰입니다.");
            }
        };
    }

    /**
     * 요청에서 JWT 토큰을 추출합니다.
     */
    private String extractTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }

    /**
     * JWT 토큰을 검증하고 클레임을 반환합니다.
     */
    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * 클레임에서 테넌트 ID를 추출합니다.
     */
    private String getTenantId(Claims claims) {
        String tenantId = claims.get("tenantId", String.class);
        return tenantId != null ? tenantId : "default";
    }

    /**
     * 인증이 필요하지 않은 공개 경로를 확인합니다.
     */
    private boolean isPublicPath(String path) {
        List<String> publicPaths = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/oauth/google",
            "/api/auth/refresh",
            "/health",
            "/actuator"
        );
        
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    /**
     * 인증 실패 시 응답을 처리합니다.
     */
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message);
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
        // 설정 클래스 (필요시 확장)
    }
}

package com.roimsg.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 보안 헤더 필터
 * KISA 보안 가이드라인에 따라 보안 헤더를 추가합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Component
public class SecurityHeadersFilter extends AbstractGatewayFilterFactory<SecurityHeadersFilter.Config> {

    @Value("${security.headers.content-security-policy:default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'}")
    private String contentSecurityPolicy;

    @Value("${security.headers.hsts-max-age:31536000}")
    private String hstsMaxAge;

    @Value("${security.headers.x-frame-options:DENY}")
    private String xFrameOptions;

    @Value("${security.headers.x-content-type-options:nosniff}")
    private String xContentTypeOptions;

    public SecurityHeadersFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                
                // Content Security Policy
                response.getHeaders().add("Content-Security-Policy", contentSecurityPolicy);
                
                // HTTP Strict Transport Security
                response.getHeaders().add("Strict-Transport-Security", 
                    "max-age=" + hstsMaxAge + "; includeSubDomains; preload");
                
                // X-Frame-Options
                response.getHeaders().add("X-Frame-Options", xFrameOptions);
                
                // X-Content-Type-Options
                response.getHeaders().add("X-Content-Type-Options", xContentTypeOptions);
                
                // X-XSS-Protection
                response.getHeaders().add("X-XSS-Protection", "1; mode=block");
                
                // Referrer-Policy
                response.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                
                // Permissions-Policy
                response.getHeaders().add("Permissions-Policy", 
                    "geolocation=(), microphone=(), camera=(), payment=(), usb=(), magnetometer=(), gyroscope=(), speaker=()");
                
                // Cache-Control (민감한 정보 보호)
                if (exchange.getRequest().getPath().value().contains("/api/")) {
                    response.getHeaders().add("Cache-Control", "no-store, no-cache, must-revalidate, private");
                    response.getHeaders().add("Pragma", "no-cache");
                    response.getHeaders().add("Expires", "0");
                }
            }));
        };
    }

    public static class Config {
        // 설정 클래스 (필요시 확장)
    }
}

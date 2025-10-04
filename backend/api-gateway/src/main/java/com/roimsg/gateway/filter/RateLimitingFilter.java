package com.roimsg.gateway.filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Rate Limiting 필터
 * Redis를 사용하여 API 호출 빈도를 제한합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(value = "rate-limit.enabled", havingValue = "true", matchIfMissing = false)
@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${rate-limit.window-ms:900000}")
    private long windowMs;

    @Value("${rate-limit.max-requests:100}")
    private int maxRequests;

    public RateLimitingFilter(@Qualifier("customReactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 클라이언트 식별자 생성 (IP 주소 또는 사용자 ID)
            String clientId = getClientId(request);
            String key = "rate_limit:" + clientId;

            return redisTemplate.opsForValue().get(key)
                .flatMap(currentCount -> {
                    int count = currentCount != null ? Integer.parseInt(currentCount) : 0;
                    
                    if (count >= maxRequests) {
                        return handleRateLimitExceeded(response);
                    }
                    
                    // 카운터 증가
                    return redisTemplate.opsForValue().increment(key)
                        .flatMap(newCount -> {
                            if (newCount == 1) {
                                // 첫 번째 요청인 경우 TTL 설정
                                return redisTemplate.expire(key, Duration.ofMillis(windowMs))
                                    .then(chain.filter(exchange));
                            }
                            return chain.filter(exchange);
                        });
                })
                .switchIfEmpty(
                    // 키가 없는 경우 새로 생성
                    redisTemplate.opsForValue().set(key, "1", Duration.ofMillis(windowMs))
                        .then(chain.filter(exchange))
                );
        };
    }

    /**
     * 클라이언트 식별자를 생성합니다.
     */
    private String getClientId(ServerHttpRequest request) {
        // X-Forwarded-For 헤더 확인 (프록시 환경)
        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            return forwardedFor.split(",")[0].trim();
        }
        
        // X-Real-IP 헤더 확인
        String realIp = request.getHeaders().getFirst("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        
        // 기본적으로 원격 주소 사용
        return request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    /**
     * Rate Limit 초과 시 응답을 처리합니다.
     */
    private Mono<Void> handleRateLimitExceeded(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("Retry-After", String.valueOf(windowMs / 1000));
        
        String body = String.format(
            "{\"error\":\"Too Many Requests\",\"message\":\"요청 한도를 초과했습니다. %d초 후 다시 시도해주세요.\",\"retryAfter\":%d}",
            windowMs / 1000,
            windowMs / 1000
        );
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
        // 설정 클래스 (필요시 확장)
    }
}

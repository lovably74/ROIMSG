package com.roimsg.gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 * Rate Limiting과 캐싱을 위한 Redis 설정을 제공합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(value = "rate-limit.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
public class RedisConfig {

    @Bean(name = "customReactiveRedisTemplate")
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            @Qualifier("reactiveRedisConnectionFactory") ReactiveRedisConnectionFactory connectionFactory) {
        
        StringRedisSerializer serializer = new StringRedisSerializer();
        
        RedisSerializationContext<String, String> serializationContext = 
            RedisSerializationContext.<String, String>newSerializationContext()
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();
        
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}

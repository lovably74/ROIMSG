package com.roimsg.user;

import com.roimsg.user.entity.User;
import com.roimsg.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("roimsg")
            .withUsername("test")
            .withPassword("test");

    static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("jwt.secret", () -> SECRET);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    UUID tenantId;
    UUID userId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        tenantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        User u = new User();
        u.setTenantId(tenantId);
        u.setGoogleId("gid1");
        u.setEmail("u1@example.com");
        u.setName("User One");
        u.setIsActive(true);
        u.setLastLoginAt(LocalDateTime.now());
        userRepository.save(u);
        userId = u.getId();
    }

    String makeAccessToken(UUID uid, UUID tid) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        return Jwts.builder()
                .setSubject(uid.toString())
                .claim("tenantId", tid.toString())
                .claim("email", "u1@example.com")
                .claim("name", "User One")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 3600_000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @Test
    void me_returns_user_when_valid_token() throws Exception {
        String token = makeAccessToken(userId, tenantId);
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("u1@example.com"))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    void update_updates_profile_fields() throws Exception {
        String token = makeAccessToken(userId, tenantId);
        String body = "{\"phoneNumber\":\"010-0000-0000\",\"address\":\"Seoul\"}";
        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.address").value("Seoul"));
    }
}
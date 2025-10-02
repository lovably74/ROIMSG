package com.roimsg.message;

import com.roimsg.message.repository.MessageRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class MessageControllerIT {

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
    MessageRepository repo;

    UUID tenantId;
    UUID userId;
    UUID peerId;

    @BeforeEach
    void setup() {
        repo.deleteAll();
        tenantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        userId = UUID.randomUUID();
        peerId = UUID.randomUUID();
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
    void send_and_fetch_conversation() throws Exception {
        String token = makeAccessToken(userId, tenantId);

        mockMvc.perform(post("/api/messages")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientId\":\"" + peerId + "\",\"content\":\"hi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("hi"));

        mockMvc.perform(get("/api/messages")
                        .header("Authorization", "Bearer " + token)
                        .param("peerId", peerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("hi"));
    }
}
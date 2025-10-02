package com.roimsg.dashboard;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class SummaryControllerIT {

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
        registry.add("jwt.secret", () -> SECRET);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbc;

    UUID tenantId;

    @BeforeEach
    void setup() {
        tenantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        // Create minimal tables if not exist
        jdbc.execute("CREATE TABLE IF NOT EXISTS users (id uuid, tenant_id uuid, email text)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS posts (id uuid, tenant_id uuid, title text)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS messages (id uuid, tenant_id uuid, content text)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS files (id uuid, tenant_id uuid, original_filename text)");
        // cleanup
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM posts");
        jdbc.update("DELETE FROM messages");
        jdbc.update("DELETE FROM files");
        // insert sample rows for tenant
        jdbc.update("INSERT INTO users(id, tenant_id, email) VALUES (?,?,?)", UUID.randomUUID(), tenantId, "u1@example.com");
        jdbc.update("INSERT INTO posts(id, tenant_id, title) VALUES (?,?,?)", UUID.randomUUID(), tenantId, "p1");
        jdbc.update("INSERT INTO messages(id, tenant_id, content) VALUES (?,?,?)", UUID.randomUUID(), tenantId, "m1");
        jdbc.update("INSERT INTO files(id, tenant_id, original_filename) VALUES (?,?,?)", UUID.randomUUID(), tenantId, "f1.txt");
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
    void returns_counts_by_tenant() throws Exception {
        String token = makeAccessToken(UUID.randomUUID(), tenantId);
        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").value(1))
                .andExpect(jsonPath("$.posts").value(1))
                .andExpect(jsonPath("$.messages").value(1))
                .andExpect(jsonPath("$.files").value(1));
    }
}
package com.roimsg.file;

import com.roimsg.file.repository.FileResourceRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class FileControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("roimsg")
            .withUsername("test")
            .withPassword("test");

    static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    static File tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) throws Exception {
        tempDir = Files.createTempDirectory("uploads-test").toFile();
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("jwt.secret", () -> SECRET);
        registry.add("file.upload.path", () -> tempDir.getAbsolutePath());
        registry.add("file.upload.allowed-types", () -> "text/plain");
        registry.add("file.upload.max-size", () -> 10485760);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FileResourceRepository repo;

    UUID tenantId;
    UUID userId;

    @BeforeEach
    void setup() {
        repo.deleteAll();
        tenantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        userId = UUID.randomUUID();
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
    void upload_and_download_file() throws Exception {
        String token = makeAccessToken(userId, tenantId);

        MockMultipartFile mf = new MockMultipartFile(
                "file", "hello.txt", "text/plain", "hello world".getBytes()
        );

        var uploadRes = mockMvc.perform(multipart("/api/files/upload")
                        .file(mf)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String body = uploadRes.getResponse().getContentAsString();
        String id = body.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/files/" + id + "/download")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")));
    }
}
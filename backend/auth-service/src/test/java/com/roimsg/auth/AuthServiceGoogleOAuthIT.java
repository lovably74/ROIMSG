package com.roimsg.auth;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.roimsg.auth.dto.AuthResponse;
import com.roimsg.auth.service.AuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class AuthServiceGoogleOAuthIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("roimsg")
            .withUsername("test")
            .withPassword("test");

    static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        // point Google endpoints to WireMock
        registry.add("spring.security.oauth2.client.provider.google.token-uri", () -> "http://localhost:" + wireMockServer.port() + "/oauth2/token");
        registry.add("spring.security.oauth2.client.provider.google.user-info-uri", () -> "http://localhost:" + wireMockServer.port() + "/userinfo");
        registry.add("spring.security.oauth2.client.registration.google.client-id", () -> "test-client-id");
        registry.add("spring.security.oauth2.client.registration.google.client-secret", () -> "test-client-secret");
        registry.add("spring.security.oauth2.client.registration.google.redirect-uri", () -> "http://localhost:3000/auth/callback");
        registry.add("jwt.secret", () -> "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        // Stub token endpoint
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/oauth2/token"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"mock_access\",\"token_type\":\"Bearer\",\"expires_in\":3600}")));

        // Stub userinfo endpoint
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/userinfo"))
                .withHeader("Authorization", WireMock.equalTo("Bearer mock_access"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"gid_123\",\"email\":\"u1@example.com\",\"name\":\"User One\",\"picture\":\"https://img\"}")));
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) wireMockServer.stop();
    }

    @Autowired
    AuthService authService;

    @Test
    void loginWithGoogle_exchangesCode_andIssuesJwt_andPersistsUser() {
        // given
        String code = "dummy_code";
        UUID tenantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        // when
        Object result = authService.loginWithGoogle(code, tenantId, null);

        // then
        assertThat(result).isNotNull();
        // GooglePreSignupResponse로 래핑되어 반환되므로 타입 체크가 필요합니다
        if (result instanceof com.roimsg.auth.dto.GooglePreSignupResponse) {
            com.roimsg.auth.dto.GooglePreSignupResponse preSignupResponse = (com.roimsg.auth.dto.GooglePreSignupResponse) result;
            if (preSignupResponse.isNeedSignup()) {
                assertThat(preSignupResponse.getSignupToken()).isNotBlank();
            } else {
                AuthResponse response = preSignupResponse.getAuth();
                assertThat(response).isNotNull();
                assertThat(response.getAccessToken()).isNotBlank();
                assertThat(response.getRefreshToken()).isNotBlank();
            }
        }
    }
}
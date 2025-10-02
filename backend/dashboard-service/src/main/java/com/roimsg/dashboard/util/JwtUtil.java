package com.roimsg.dashboard.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey key() { return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)); }

    private Claims parse(String token) { return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload(); }

    public boolean validate(String token) { try { return parse(token).getExpiration().after(new Date()); } catch (Exception e) { return false; } }

    public UUID userId(String token) { return UUID.fromString(parse(token).getSubject()); }
    public UUID tenantId(String token) { String t = parse(token).get("tenantId", String.class); return t==null?null:UUID.fromString(t); }
}
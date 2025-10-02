package com.roimsg.dashboard.controller;

import com.roimsg.dashboard.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class SummaryController {

    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwt;

    public SummaryController(JdbcTemplate jdbcTemplate, JwtUtil jwt) {
        this.jdbcTemplate = jdbcTemplate;
        this.jwt = jwt;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestHeader("Authorization") String authorization) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        UUID tid = jwt.tenantId(token);

        Map<String, Object> result = new HashMap<>();
        long users = count("users", tid);
        long posts = count("posts", tid);
        long messages = count("messages", tid);
        long files = count("files", tid);
        result.put("users", users);
        result.put("posts", posts);
        result.put("messages", messages);
        result.put("files", files);
        return ResponseEntity.ok(result);
    }

    private long count(String table, UUID tenantId) {
        try {
            Long v = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE tenant_id = ?", Long.class, tenantId);
            return v != null ? v : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private String extract(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw new RuntimeException("No token");
        return authorization.substring(7);
    }
}
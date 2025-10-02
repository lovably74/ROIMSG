package com.roimsg.user.controller;

import com.roimsg.user.entity.User;
import com.roimsg.user.repository.UserRepository;
import com.roimsg.user.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authorization) {
        String token = extract(authorization);
        if (!jwtUtil.validate(token)) return ResponseEntity.status(401).build();
        UUID uid = jwtUtil.getUserId(token);
        UUID tid = jwtUtil.getTenantId(token);
        Optional<User> user = userRepository.findByIdAndTenantId(uid, tid);
        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authorization,
                                    @PathVariable("id") UUID id,
                                    @Valid @RequestBody Map<String, Object> payload) {
        String token = extract(authorization);
        if (!jwtUtil.validate(token)) return ResponseEntity.status(401).build();
        UUID uid = jwtUtil.getUserId(token);
        UUID tid = jwtUtil.getTenantId(token);
        if (!uid.equals(id)) return ResponseEntity.status(403).build();

        Optional<User> userOpt = userRepository.findByIdAndTenantId(uid, tid);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User u = userOpt.get();
        if (payload.containsKey("phoneNumber")) u.setPhoneNumber((String) payload.get("phoneNumber"));
        if (payload.containsKey("address")) u.setAddress((String) payload.get("address"));
        userRepository.save(u);
        return ResponseEntity.ok(u);
    }

    private String extract(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw new RuntimeException("No token");
        return authorization.substring(7);
    }
}
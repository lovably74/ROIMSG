package com.roimsg.board.controller;

import com.roimsg.board.entity.Post;
import com.roimsg.board.repository.PostRepository;
import com.roimsg.board.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
public class PostController {

    private final PostRepository postRepository;
    private final JwtUtil jwt;

    public PostController(PostRepository postRepository, JwtUtil jwt) {
        this.postRepository = postRepository;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader("Authorization") String authorization) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        var tid = jwt.tenantId(token);
        var list = postRepository.findByTenantIdOrderByCreatedAtDesc(tid);
        return ResponseEntity.ok(list);
    }

    public record CreatePostReq(@NotBlank String title, @NotBlank String content) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authorization, @Valid @RequestBody CreatePostReq body) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        var tid = jwt.tenantId(token);
        var uid = jwt.userId(token);
        Post p = new Post();
        p.setTenantId(tid);
        p.setAuthorId(uid);
        p.setTitle(body.title());
        p.setContent(body.content());
        postRepository.save(p);
        return ResponseEntity.ok(p);
    }

    private String extract(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw new RuntimeException("No token");
        return authorization.substring(7);
    }
}
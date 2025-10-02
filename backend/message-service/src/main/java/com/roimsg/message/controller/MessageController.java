package com.roimsg.message.controller;

import com.roimsg.message.entity.Message;
import com.roimsg.message.repository.MessageRepository;
import com.roimsg.message.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository repo;
    private final JwtUtil jwt;

    public MessageController(MessageRepository repo, JwtUtil jwt) {
        this.repo = repo;
        this.jwt = jwt;
    }

    public record SendMessageReq(@NotNull UUID recipientId, @NotBlank String content) {}

    @PostMapping
    public ResponseEntity<?> send(@RequestHeader("Authorization") String authorization,
                                  @Valid @RequestBody SendMessageReq body) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        UUID tid = jwt.tenantId(token);
        UUID uid = jwt.userId(token);
        Message m = new Message();
        m.setTenantId(tid);
        m.setSenderId(uid);
        m.setRecipientId(body.recipientId());
        m.setContent(body.content());
        repo.save(m);
        return ResponseEntity.ok(m);
    }

    @GetMapping
    public ResponseEntity<?> conversation(@RequestHeader("Authorization") String authorization,
                                          @RequestParam("peerId") UUID peerId) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        UUID tid = jwt.tenantId(token);
        UUID uid = jwt.userId(token);
        List<Message> list = repo.findConversation(tid, uid, peerId);
        return ResponseEntity.ok(list);
    }

    private String extract(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw new RuntimeException("No token");
        return authorization.substring(7);
    }
}
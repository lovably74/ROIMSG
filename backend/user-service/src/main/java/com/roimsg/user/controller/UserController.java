package com.roimsg.user.controller;

import com.roimsg.common.web.ApiResponse;
import com.roimsg.user.entity.User;
import com.roimsg.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> list(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.ok(userService.list(q)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> get(@PathVariable UUID id) {
        return userService.get(id)
                .map(u -> ResponseEntity.ok(ApiResponse.ok(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable UUID id, @RequestBody User data) {
        return userService.update(id, data)
                .map(u -> ResponseEntity.ok(ApiResponse.ok(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

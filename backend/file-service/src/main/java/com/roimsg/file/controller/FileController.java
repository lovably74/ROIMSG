package com.roimsg.file.controller;

import com.roimsg.file.entity.FileResource;
import com.roimsg.file.repository.FileResourceRepository;
import com.roimsg.file.util.JwtUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileResourceRepository repo;
    private final JwtUtil jwt;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.max-size}")
    private long maxSize;

    public FileController(FileResourceRepository repo, JwtUtil jwt) {
        this.repo = repo;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader("Authorization") String authorization) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        UUID tid = jwt.tenantId(token);
        List<FileResource> list = repo.findByTenantIdOrderByCreatedAtDesc(tid);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestHeader("Authorization") String authorization,
                                    @RequestPart("file") @NotNull MultipartFile file) throws IOException {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        UUID tid = jwt.tenantId(token);
        UUID uid = jwt.userId(token);

        if (file.getSize() > maxSize) return ResponseEntity.badRequest().body("File too large");
        String contentType = file.getContentType();
        if (StringUtils.hasText(allowedTypes)) {
            boolean ok = false;
            for (String t : allowedTypes.split(",")) {
                if (contentType != null && contentType.toLowerCase().contains(t.trim().toLowerCase())) { ok = true; break; }
            }
            if (!ok) return ResponseEntity.badRequest().body("Disallowed file type");
        }

        UUID fid = UUID.randomUUID();
        String stored = fid.toString();
        Path tenantDir = Paths.get(uploadPath, tid.toString());
        Files.createDirectories(tenantDir);
        Path dest = tenantDir.resolve(stored);
        file.transferTo(dest.toFile());

        FileResource fr = new FileResource();
        fr.setTenantId(tid);
        fr.setUploaderId(uid);
        fr.setOriginalFilename(file.getOriginalFilename());
        fr.setStoredFilename(stored);
        fr.setContentType(file.getContentType());
        fr.setSize(file.getSize());
        repo.save(fr);

        return ResponseEntity.ok(fr);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@RequestHeader("Authorization") String authorization,
                                      @PathVariable("id") UUID id) {
        String token = extract(authorization);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        UUID tid = jwt.tenantId(token);
        return repo.findByIdAndTenantId(id, tid)
                .map(fr -> {
                    try {
                        Path path = Paths.get(uploadPath, tid.toString(), fr.getStoredFilename());
                        FileSystemResource res = new FileSystemResource(path);
                        String filename = fr.getOriginalFilename() != null ? fr.getOriginalFilename() : fr.getStoredFilename();
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                                .contentType(MediaType.parseMediaType(fr.getContentType() != null ? fr.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                                .contentLength(res.contentLength())
                                .body(res);
                    } catch (Exception ex) {
                        return ResponseEntity.internalServerError().body("Failed to read file");
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String extract(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw new RuntimeException("No token");
        return authorization.substring(7);
    }
}
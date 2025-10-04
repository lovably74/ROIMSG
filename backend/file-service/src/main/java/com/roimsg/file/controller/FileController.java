package com.roimsg.file.controller;

import com.roimsg.common.web.ApiResponse;
import com.roimsg.file.entity.StoredFile;
import com.roimsg.file.repository.StoredFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final StoredFileRepository repository;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.denied-extensions}")
    private String deniedExtensions;

    public FileController(StoredFileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoredFile>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(repository.findAll()));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StoredFile>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("empty_file"));
        }
        String original = Objects.requireNonNull(file.getOriginalFilename());
        String ext = getExtension(original).toLowerCase(Locale.ROOT);
        if (isDenied(ext)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("denied_extension"));
        }
        Path baseDir = Paths.get(uploadPath);
        if (!Files.exists(baseDir)) Files.createDirectories(baseDir);
        String storedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
        Path target = baseDir.resolve(storedName);
        Files.copy(file.getInputStream(), target);

        StoredFile sf = new StoredFile();
        sf.setOriginalName(original);
        sf.setFileName(storedName);
        sf.setFileSize(file.getSize());
        sf.setMimeType(Optional.ofNullable(file.getContentType()).orElse("application/octet-stream"));
        sf.setFilePath(target.toAbsolutePath().toString());
        StoredFile saved = repository.save(sf);
        return ResponseEntity.ok(ApiResponse.ok(saved));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) throws IOException {
        Optional<StoredFile> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        StoredFile sf = opt.get();
        Path p = Paths.get(sf.getFilePath());
        if (!Files.exists(p)) return ResponseEntity.notFound().build();
        byte[] data = Files.readAllBytes(p);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sf.getOriginalName() + "\"")
                .contentType(MediaType.parseMediaType(sf.getMimeType()))
                .body(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws IOException {
        Optional<StoredFile> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        StoredFile sf = opt.get();
        Path p = Paths.get(sf.getFilePath());
        repository.deleteById(id);
        if (Files.exists(p)) Files.delete(p);
        return ResponseEntity.noContent().build();
    }

    private boolean isDenied(String ext) {
        if (ext == null) return true;
        Set<String> denied = new HashSet<>();
        for (String e : deniedExtensions.split(",")) denied.add(e.trim().toLowerCase(Locale.ROOT));
        return denied.contains(ext);
    }

    private String getExtension(String filename) {
        String name = StringUtils.getFilename(filename);
        if (name == null) return "";
        int idx = name.lastIndexOf('.');
        return idx == -1 ? "" : name.substring(idx + 1);
    }
}

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
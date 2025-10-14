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
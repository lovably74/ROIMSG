package com.roimsg.file.repository;

import com.roimsg.file.entity.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileResourceRepository extends JpaRepository<FileResource, UUID> {
    List<FileResource> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    Optional<FileResource> findByIdAndTenantId(UUID id, UUID tenantId);
}
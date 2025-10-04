package com.roimsg.user.repository;

import com.roimsg.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findByEmailContainingIgnoreCase(String email);
    Optional<User> findByIdAndTenantId(UUID id, UUID tenantId);
}

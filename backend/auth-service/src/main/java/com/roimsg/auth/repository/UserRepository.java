package com.roimsg.auth.repository;

import com.roimsg.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 리포지토리
 * 사용자 데이터 접근을 위한 JPA 리포지토리입니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Google ID로 사용자 조회
     */
    Optional<User> findByGoogleId(String googleId);

    /**
     * 테넌트 ID와 이메일로 사용자 조회
     */
    Optional<User> findByTenantIdAndEmail(UUID tenantId, String email);

    /**
     * 테넌트 ID와 Google ID로 사용자 조회
     */
    Optional<User> findByTenantIdAndGoogleId(UUID tenantId, String googleId);

    /**
     * 테넌트 ID로 활성 사용자 목록 조회
     */
    List<User> findByTenantIdAndIsActiveTrue(UUID tenantId);

    /**
     * 이메일로 사용자 조회 (모든 테넌트)
     */
    List<User> findByEmail(String email);

    /**
     * 테넌트 ID로 사용자 수 조회
     */
    long countByTenantIdAndIsActiveTrue(UUID tenantId);

    /**
     * 이름으로 사용자 검색 (테넌트별)
     */
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.isActive = true AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<User> searchByTenantIdAndName(@Param("tenantId") UUID tenantId, @Param("name") String name);

    /**
     * 최근 로그인한 사용자 조회 (테넌트별)
     */
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.isActive = true " +
           "ORDER BY u.lastLoginAt DESC")
    List<User> findRecentUsersByTenantId(@Param("tenantId") UUID tenantId);

    /**
     * 활성 사용자 존재 여부 확인
     */
    boolean existsByTenantIdAndEmailAndIsActiveTrue(UUID tenantId, String email);

    /**
     * Google ID 존재 여부 확인
     */
    boolean existsByGoogleId(String googleId);
}

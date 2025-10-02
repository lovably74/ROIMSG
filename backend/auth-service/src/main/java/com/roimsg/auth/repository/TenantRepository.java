package com.roimsg.auth.repository;

import com.roimsg.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 테넌트 리포지토리
 * 테넌트 데이터 접근을 위한 JPA 리포지토리입니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    /**
     * 도메인으로 테넌트 조회
     */
    Optional<Tenant> findByDomain(String domain);

    /**
     * 이름으로 테넌트 조회
     */
    Optional<Tenant> findByName(String name);

    /**
     * 활성 테넌트 목록 조회
     */
    List<Tenant> findByIsActiveTrue();

    /**
     * 구독 플랜별 테넌트 조회
     */
    List<Tenant> findBySubscriptionPlanAndIsActiveTrue(Tenant.SubscriptionPlan subscriptionPlan);

    /**
     * 이름으로 테넌트 검색
     */
    @Query("SELECT t FROM Tenant t WHERE t.isActive = true AND " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tenant> searchByName(@Param("name") String name);

    /**
     * 도메인 존재 여부 확인
     */
    boolean existsByDomain(String domain);

    /**
     * 이름 존재 여부 확인
     */
    boolean existsByName(String name);

    /**
     * 활성 테넌트 수 조회
     */
    long countByIsActiveTrue();

    /**
     * 구독 플랜별 테넌트 수 조회
     */
    long countBySubscriptionPlanAndIsActiveTrue(Tenant.SubscriptionPlan subscriptionPlan);
}

package com.roimsg.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 테넌트 엔티티
 * 멀티테넌시를 위한 테넌트 정보를 저장합니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenants_domain", columnList = "domain"),
    @Index(name = "idx_tenants_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "테넌트 이름은 필수입니다.")
    @Size(max = 100, message = "테넌트 이름은 100자를 초과할 수 없습니다.")
    private String name;

    @Column(name = "domain", unique = true)
    @Size(max = 255, message = "도메인은 255자를 초과할 수 없습니다.")
    private String domain;

    @Column(name = "description")
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    private String description;

    @Column(name = "logo_url")
    @Size(max = 500, message = "로고 URL은 500자를 초과할 수 없습니다.")
    private String logoUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "subscription_plan")
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.BASIC;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자
    public Tenant() {}

    // 생성자
    public Tenant(String name, String domain) {
        this.name = name;
        this.domain = domain;
        this.isActive = true;
        this.subscriptionPlan = SubscriptionPlan.BASIC;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", isActive=" + isActive +
                ", subscriptionPlan=" + subscriptionPlan +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * 구독 플랜 열거형
     */
    public enum SubscriptionPlan {
        BASIC("기본"),
        PREMIUM("프리미엄"),
        ENTERPRISE("엔터프라이즈");

        private final String description;

        SubscriptionPlan(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}

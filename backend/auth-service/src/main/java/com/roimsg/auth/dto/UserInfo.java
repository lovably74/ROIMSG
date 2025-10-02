package com.roimsg.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 정보 DTO
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Schema(description = "사용자 정보")
public class UserInfo {

    @JsonProperty("id")
    @Schema(description = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @JsonProperty("tenant_id")
    @Schema(description = "테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID tenantId;

    @JsonProperty("google_id")
    @Schema(description = "Google ID", example = "1234567890")
    private String googleId;

    @JsonProperty("email")
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @JsonProperty("name")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @JsonProperty("profile_image_url")
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @JsonProperty("phone_number")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @JsonProperty("address")
    @Schema(description = "주소", example = "서울시 강남구")
    private String address;

    @JsonProperty("custom_profile_image_url")
    @Schema(description = "커스텀 프로필 이미지 URL", example = "https://example.com/custom-profile.jpg")
    private String customProfileImageUrl;

    @JsonProperty("is_active")
    @Schema(description = "활성 상태", example = "true")
    private Boolean isActive;

    @JsonProperty("last_login_at")
    @Schema(description = "마지막 로그인 시간", example = "2023-10-01T10:00:00")
    private LocalDateTime lastLoginAt;

    @JsonProperty("created_at")
    @Schema(description = "생성 시간", example = "2023-10-01T10:00:00")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "수정 시간", example = "2023-10-01T10:00:00")
    private LocalDateTime updatedAt;

    // 기본 생성자
    public UserInfo() {}

    // 생성자
    public UserInfo(UUID id, UUID tenantId, String googleId, String email, String name) {
        this.id = id;
        this.tenantId = tenantId;
        this.googleId = googleId;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomProfileImageUrl() {
        return customProfileImageUrl;
    }

    public void setCustomProfileImageUrl(String customProfileImageUrl) {
        this.customProfileImageUrl = customProfileImageUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
        return "UserInfo{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", googleId='" + googleId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}

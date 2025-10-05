package com.roimsg.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Google OAuth 사용자 정보 DTO
 * Google API에서 반환되는 사용자 정보를 담는 클래스입니다.
 * 
 * @author ROIMSG Development Team
 * @version 1.0.0
 */
@Schema(description = "Google OAuth 사용자 정보")
public class GoogleUser {

    @JsonProperty("id")
    @Schema(description = "Google 사용자 ID", example = "123456789012345678901")
    private String id;

    @JsonProperty("email")
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @JsonProperty("name")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @JsonProperty("picture")
    @Schema(description = "프로필 이미지 URL", example = "https://lh3.googleusercontent.com/...")
    private String picture;

    // 기본 생성자
    public GoogleUser() {}

    // 전체 생성자
    public GoogleUser(String id, String email, String name, String picture) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    // Getter 메서드들
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    // Setter 메서드들
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "GoogleUser{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}

package com.roimsg.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google 프로필 요약")
public class GoogleProfile {

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("picture")
    private String picture;

    public GoogleProfile() {}

    public GoogleProfile(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPicture() { return picture; }

    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setPicture(String picture) { this.picture = picture; }
}
package com.roimsg.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google 로그인 사전 응답 (회원가입 필요 시)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GooglePreSignupResponse {

    @JsonProperty("authenticated")
    private boolean authenticated;

    @JsonProperty("needSignup")
    private boolean needSignup;

    @JsonProperty("auth")
    private AuthResponse auth; // authenticated=true일 때만 세팅

    @JsonProperty("signupToken")
    private String signupToken; // needSignup=true일 때만 세팅

    @JsonProperty("profile")
    private GoogleProfile profile; // needSignup=true일 때만 세팅

    public GooglePreSignupResponse() {}

    public static GooglePreSignupResponse authenticated(AuthResponse auth) {
        GooglePreSignupResponse r = new GooglePreSignupResponse();
        r.authenticated = true;
        r.needSignup = false;
        r.auth = auth;
        return r;
    }

    public static GooglePreSignupResponse needSignup(String signupToken, GoogleProfile profile) {
        GooglePreSignupResponse r = new GooglePreSignupResponse();
        r.authenticated = false;
        r.needSignup = true;
        r.signupToken = signupToken;
        r.profile = profile;
        return r;
    }

    public boolean isAuthenticated() { return authenticated; }
    public boolean isNeedSignup() { return needSignup; }
    public AuthResponse getAuth() { return auth; }
    public String getSignupToken() { return signupToken; }
    public GoogleProfile getProfile() { return profile; }

    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    public void setNeedSignup(boolean needSignup) { this.needSignup = needSignup; }
    public void setAuth(AuthResponse auth) { this.auth = auth; }
    public void setSignupToken(String signupToken) { this.signupToken = signupToken; }
    public void setProfile(GoogleProfile profile) { this.profile = profile; }
}
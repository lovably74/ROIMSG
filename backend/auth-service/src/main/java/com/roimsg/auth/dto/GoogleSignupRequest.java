package com.roimsg.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google 회원가입 요청")
public class GoogleSignupRequest {

    @JsonProperty("signupToken")
    @Schema(description = "사전 검증된 회원가입 토큰", required = true)
    private String signupToken;

    @JsonProperty("agreePrivacy")
    @Schema(description = "개인정보 처리방침 동의", required = true)
    private boolean agreePrivacy;

    @JsonProperty("agreeTerms")
    @Schema(description = "이용약관 동의", required = false)
    private boolean agreeTerms;

    @JsonProperty("name")
    @Schema(description = "표시 이름 (선택적으로 수정 가능)")
    private String name;

    public String getSignupToken() { return signupToken; }
    public boolean isAgreePrivacy() { return agreePrivacy; }
    public boolean isAgreeTerms() { return agreeTerms; }
    public String getName() { return name; }

    public void setSignupToken(String signupToken) { this.signupToken = signupToken; }
    public void setAgreePrivacy(boolean agreePrivacy) { this.agreePrivacy = agreePrivacy; }
    public void setAgreeTerms(boolean agreeTerms) { this.agreeTerms = agreeTerms; }
    public void setName(String name) { this.name = name; }
}
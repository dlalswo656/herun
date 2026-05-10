package com.herun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SignupRequest {
        private String username;
        private String email;
        private String password;
        private Integer age;
        private Double weight;
        private Double height;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TokenResponse {
        private String token;
        private Long userId;
        private String username;
        private String email;
        private String role;
    }
}

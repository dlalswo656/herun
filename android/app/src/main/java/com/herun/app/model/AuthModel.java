package com.herun.app.model;

public class AuthModel {

    public static class SignupRequest {
        public String username;
        public String email;
        public String password;
        public Integer age;
        public Double weight;
        public Double height;

        public SignupRequest(String username, String email, String password,
                             Integer age, Double weight, Double height) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.age = age;
            this.weight = weight;
            this.height = height;
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class TokenResponse {
        public String token;
        public Long userId;
        public String username;
        public String email;
        public String role;
    }
}

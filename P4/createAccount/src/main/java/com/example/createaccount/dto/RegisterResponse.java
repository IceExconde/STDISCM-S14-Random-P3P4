package com.example.createaccount.dto;

public class RegisterResponse {
    private String message;
    private String token;
    private String userId;

    public RegisterResponse(String message, String token, String userId) {
        this.message = message;
        this.token = token;
        this.userId = userId;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

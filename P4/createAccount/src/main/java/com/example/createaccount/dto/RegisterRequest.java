package com.example.createaccount.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String name;
    
    @NotBlank
    @Pattern(regexp = "STUDENT|FACULTY", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

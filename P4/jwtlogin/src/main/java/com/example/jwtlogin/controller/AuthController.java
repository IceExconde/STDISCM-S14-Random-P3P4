package com.example.jwtlogin.controller;

import com.example.jwtlogin.model.User;
import com.example.jwtlogin.service.UserService;
import com.example.jwtlogin.service.JwtService;
import com.example.jwtlogin.dto.LoginRequest;
import com.example.jwtlogin.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }

        String token = jwtService.generateToken(user);  // Generate token after successful authentication
        return ResponseEntity.ok(new LoginResponse("Login successful! You are a " + user.getRole().name().toLowerCase(), token));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }

    
}

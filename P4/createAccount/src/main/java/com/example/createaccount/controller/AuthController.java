package com.example.createaccount.controller;

import com.example.createaccount.model.User;
import com.example.createaccount.service.UserService;
import com.example.createaccount.service.JwtService;
import com.example.createaccount.dto.RegisterRequest;
import com.example.createaccount.dto.RegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);
            String token = jwtService.generateToken(user);
            
            return ResponseEntity.ok(new RegisterResponse(
                "Registration successful! You are a " + user.getRole().name().toLowerCase(),
                token,
                user.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}

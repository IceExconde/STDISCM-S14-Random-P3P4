package com.example.jwtlogin.service;

import com.example.jwtlogin.model.User;
import com.example.jwtlogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Injecting the UserRepository via constructor
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    // Authenticate method to check email and password
    public User authenticate(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email);
        
        // Check if user exists and compare passwords using passwordEncoder
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user; // Valid credentials
        }
        
        return null; // Invalid credentials
    }
}

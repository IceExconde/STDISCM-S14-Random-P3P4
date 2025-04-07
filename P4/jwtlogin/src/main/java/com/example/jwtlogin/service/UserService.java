package com.example.jwtlogin.service;

import com.example.jwtlogin.model.User;
import com.example.jwtlogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Injecting the UserRepository via constructor
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Authenticate method to check email and password
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email); // Find user by email in the MongoDB collection
        if (user != null && user.getPassword().equals(password)) {
            return user; // Valid credentials
        }
        return null; // Invalid credentials
    }
}

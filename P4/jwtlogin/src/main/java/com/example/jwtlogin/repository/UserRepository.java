package com.example.jwtlogin.repository;

import com.example.jwtlogin.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email); // Custom method to find a user by email
}

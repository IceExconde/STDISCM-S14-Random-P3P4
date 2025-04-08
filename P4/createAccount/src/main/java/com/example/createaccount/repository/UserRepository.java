package com.example.createaccount.repository;

import com.example.createaccount.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email); // Find a user by their email
}

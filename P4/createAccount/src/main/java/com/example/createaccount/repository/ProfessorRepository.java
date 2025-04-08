package com.example.createaccount.repository;

import com.example.createaccount.model.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessorRepository extends MongoRepository<Professor, String> {
    
}

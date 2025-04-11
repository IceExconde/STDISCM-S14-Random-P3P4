package com.example.faculty_dropcourses.repository;

import com.example.faculty_dropcourses.model.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends MongoRepository<Professor, String> {

    // Method to find a professor by their ID
    Optional<Professor> findById(String id);
}

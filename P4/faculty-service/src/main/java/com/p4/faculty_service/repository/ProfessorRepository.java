package com.p4.faculty_service.repository;

import com.p4.faculty_service.model.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessorRepository extends MongoRepository<Professor, String> {
    // Custom query methods can be added here if needed
}

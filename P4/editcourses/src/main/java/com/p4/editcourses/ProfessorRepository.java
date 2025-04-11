package com.p4.editcourses;

import com.p4.editcourses.model.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessorRepository extends MongoRepository<Professor, String> {
    // Custom query methods can be added here if needed
}

package com.p4.faculty_service.repository;

import com.p4.faculty_service.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    // Find a student by their ID
    Optional<Student> findById(String id);

    List<Student> findByIdIn(List<String> ids);
}

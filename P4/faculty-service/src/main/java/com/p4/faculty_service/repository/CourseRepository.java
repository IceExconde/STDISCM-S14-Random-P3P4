package com.p4.faculty_service.repository;

import com.p4.faculty_service.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
    // Custom query methods can be added here if needed
}

package com.example.faculty_dropcourses.repository;

import com.example.faculty_dropcourses.model.Courses;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoursesRepository extends MongoRepository<Courses, String> {

    // Method to find a course by its ID
    Optional<Courses> findById(String id);

    List<Courses> findByFacultyId(String facultyId);
}
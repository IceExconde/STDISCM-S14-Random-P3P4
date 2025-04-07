package com.p4.faculty_service.repository;

import com.p4.faculty_service.model.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GradeRepository extends MongoRepository<Grade, String> {
    // Custom query method to find grade by studentId and courseId
    Optional<Grade> findByStudentIdAndCourseId(String studentId, String courseId);

    Optional<Grade> findByStudentIdAndCourseIdAndProfId(String studentId, String courseId, String profId);
}

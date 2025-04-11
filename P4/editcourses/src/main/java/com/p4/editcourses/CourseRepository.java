package com.p4.editcourses;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.p4.editcourses.model.Course;
import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByFacultyId(String facultyId);
    List<Course> findAllById(List<String> ids);
}
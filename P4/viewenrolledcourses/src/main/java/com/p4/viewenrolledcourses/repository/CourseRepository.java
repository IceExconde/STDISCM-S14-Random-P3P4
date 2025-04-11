package com.p4.viewenrolledcourses.repository;

import com.p4.viewenrolledcourses.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
}

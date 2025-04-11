package com.p4.createcourse.repository;

import com.p4.createcourse.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
}

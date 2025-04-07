package com.p4.enroll.repositories;

import com.p4.enroll.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface CourseRepository extends MongoRepository<Course, String> {
}

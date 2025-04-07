package com.p4.viewgrades.repositories;

import com.p4.viewgrades.models.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
}

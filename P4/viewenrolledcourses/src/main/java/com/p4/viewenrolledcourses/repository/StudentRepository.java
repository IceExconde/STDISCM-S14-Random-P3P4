package com.p4.viewenrolledcourses.repository;

import com.p4.viewenrolledcourses.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {
}

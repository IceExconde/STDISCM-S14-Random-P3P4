package com.p4.faculty_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "students")
public class Student {

    @Id
    private String id;
    private String name;
    private List<String> enrolledCourses;  // List of course IDs the student is enrolled in
    private double grade;  // Grade field to store the student's grade (optional, depending on your use case)

    // Default constructor
    public Student() {}

    // Constructor with parameters
    public Student(String name, List<String> enrolledCourses, double grade) {
        this.name = name;
        this.enrolledCourses = enrolledCourses;
        this.grade = grade;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}

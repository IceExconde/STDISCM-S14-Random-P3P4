package com.p4.faculty_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "grades")
public class Grade {
    @Id
    private String id;
    private String courseId;
    private String studentId;
    private String profId;
    private double grade;

    // Constructor
    public Grade(String courseId, String studentId, String profId, double grade) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.profId = profId;
        this.grade = grade;
    }

    // Default constructor
    public Grade() {}

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProfId() {
        return profId;
    }

    public void setProfId(String profId) {
        this.profId = profId;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}

package com.p4.viewcourses.dto;

import com.p4.viewcourses.Course;

import java.util.List;

public class CourseResponse {
    private String status;
    private String message;
    private List<Course> courses;

    // Constructor
    public CourseResponse(String status, String message, List<Course> courses) {
        this.status = status;
        this.message = message;
        this.courses = courses;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}

package com.p4.editcourses.dto;

import com.p4.editcourses.model.Course;

import java.util.List;

public class EditCourseResponse {
    private String status;
    private String message;
    private List<Course> courses;

    // Constructor
    public EditCourseResponse(String status, String message, List<Course> courses) {
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

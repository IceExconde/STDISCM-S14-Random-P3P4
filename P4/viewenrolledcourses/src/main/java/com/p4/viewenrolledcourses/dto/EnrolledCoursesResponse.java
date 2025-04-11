package com.p4.viewenrolledcourses.dto;

import com.p4.viewenrolledcourses.model.Course;

import java.util.List;

public class EnrolledCoursesResponse {
    private String message;
    private List<Course> courseList;

    public EnrolledCoursesResponse(String message, List<Course> courseList) {
        this.courseList = courseList;
        this.message = message;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

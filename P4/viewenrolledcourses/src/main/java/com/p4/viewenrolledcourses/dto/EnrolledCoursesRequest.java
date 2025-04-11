package com.p4.viewenrolledcourses.dto;

public class EnrolledCoursesRequest {
    private String studentId;

    public EnrolledCoursesRequest(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}

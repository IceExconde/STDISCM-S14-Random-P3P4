package com.p4.enroll.dto;

import org.bson.types.ObjectId;

public class EnrollRequest {
    private String courseId;
    private String studentId;
    // insert jwt token


    public EnrollRequest(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}

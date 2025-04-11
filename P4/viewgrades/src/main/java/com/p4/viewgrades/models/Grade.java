package com.p4.viewgrades.models;

import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "grades")
public class Grade {
    @Id
    private String id;
    private String courseId;
    @Field("studentId")
    private String studentId;
    private String profId;
    private double grade;

    public Grade(@NonNull String studentId, @NonNull String courseId, @NonNull Double grade, @NonNull String profId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.profId = profId;
        this.grade = grade;
    }

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

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}

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
    private double score;

    public Grade(@NonNull String studentId, @NonNull String courseId, @NonNull double score, @NonNull String profId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.profId = profId;
        this.score = score;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

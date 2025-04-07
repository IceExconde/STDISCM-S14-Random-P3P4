package com.p4.viewcourses;

import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "courses")
public class Course {
    @Id
    private String classNbr;
    private String course;
    private String section;
    private String days;
    private String time;
    private String room;
    private int count;
    private List<String> studentIds = new ArrayList<>();
    private String facultyId;


    public Course(@NonNull String classNbr, @NonNull String course, @NonNull String section, @NonNull String days, @NonNull String time, @NonNull String room, String facultyId) {
        this.classNbr = classNbr;
        this.course = course;
        this.section = section;
        this.days = days;
        this.time = time;
        this.room = room;
        this.facultyId = facultyId;
        this.count = 0;
    }

    public String getClassNbr() {
        return classNbr;
    }

    public void setClassNbr(String classNbr) {
        this.classNbr = classNbr;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
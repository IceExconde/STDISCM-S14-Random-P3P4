package com.example.faculty_dropcourses.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "courses")
public class Courses {

    @Id
    private String id;
    private String Courses;
    private String section;
    private String days;
    private String time;
    private String room;
    private int count;
    private List<String> studentIds;
    private String facultyId;

    // Constructors
    public Courses() {
    }

    public Courses(String id, String Courses, String section, String days, String time, String room, int count, List<String> studentIds, String facultyId) {
        this.id = id;
        this.Courses = Courses;
        this.section = section;
        this.days = days;
        this.time = time;
        this.room = room;
        this.count = count;
        this.studentIds = studentIds;
        this.facultyId = facultyId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourses() {
        return Courses;
    }

    public void setCourses(String Courses) {
        this.Courses = Courses;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }
}

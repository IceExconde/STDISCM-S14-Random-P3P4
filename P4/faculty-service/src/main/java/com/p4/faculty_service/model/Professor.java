package com.p4.faculty_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "professors")
public class Professor {
    @Id
    private String id;
    private String name;
    private List<String> courseIds;

    // Default constructor
    public Professor() {
    }

    // Parameterized constructor
    public Professor(String id, String name, List<String> courseIds) {
        this.id = id;
        this.name = name;
        this.courseIds = courseIds;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }
}

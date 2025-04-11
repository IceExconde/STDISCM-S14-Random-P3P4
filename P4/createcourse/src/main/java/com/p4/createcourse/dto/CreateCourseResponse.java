package com.p4.createcourse.dto;

public class CreateCourseResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreateCourseResponse(String message) {
        this.message = message;
    }
}

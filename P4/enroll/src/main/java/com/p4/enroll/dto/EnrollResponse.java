package com.p4.enroll.dto;

public class EnrollResponse {
    private String message;

    public EnrollResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.p4.viewgrades.dto;

public class ModifiedGradeDTO {
    private String classNbr;
    private String courseName;
    private Double score;

    // Constructor
    public ModifiedGradeDTO(String classNbr, String courseName, Double score) {
        this.classNbr = classNbr;
        this.courseName = courseName;
        this.score = score;
    }

    public String getClassNbr() {
        return classNbr;
    }

    public void setClassNbr(String classNbr) {
        this.classNbr = classNbr;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

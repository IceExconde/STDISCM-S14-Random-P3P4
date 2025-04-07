package com.p4.viewgrades.dto;

import java.util.List;

public class ViewGradesResponse {
    private List<ModifiedGradeDTO> grades;

    public List<ModifiedGradeDTO> getGrades() {
        return grades;
    }

    public void setGrades(List<ModifiedGradeDTO> grades) {
        this.grades = grades;
    }
}

package com.p4.faculty_service.controller;

import com.p4.faculty_service.model.Grade;
import com.p4.faculty_service.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    // Endpoint to add a grade for a student
    @PostMapping("/add")
    public ResponseEntity<Grade> addGrade(@RequestBody Grade grade) {
        try {
            // Call service method to save grade and update student
            Grade savedGrade = gradeService.addOrUpdateGrade(grade);
            return new ResponseEntity<>(savedGrade, HttpStatus.CREATED);
        } catch (Exception e) {
            // Return INTERNAL_SERVER_ERROR if any exception occurs
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all grades (optional, can be used to retrieve grades for debugging)
    @GetMapping("/all")
    public ResponseEntity<List<Grade>> getAllGrades() {
        try {
            List<Grade> grades = gradeService.getAllGrades();
            if (grades.isEmpty()) {
                // Return NO_CONTENT if no grades found
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(grades, HttpStatus.OK);
        } catch (Exception e) {
            // Return INTERNAL_SERVER_ERROR if any exception occurs
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get a grade by studentId and courseId (optional)
    @GetMapping("/{studentId}/{courseId}")
    public ResponseEntity<Grade> getGradeByStudentAndCourse(@PathVariable String studentId, @PathVariable String courseId) {
        try {
            // Retrieve grade based on studentId and courseId
            Grade grade = gradeService.getGradeByStudentAndCourse(studentId, courseId);
            if (grade == null) {
                // Return NOT_FOUND if no grade exists for the given studentId and courseId
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(grade, HttpStatus.OK);
        } catch (Exception e) {
            // Return INTERNAL_SERVER_ERROR if any exception occurs
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}

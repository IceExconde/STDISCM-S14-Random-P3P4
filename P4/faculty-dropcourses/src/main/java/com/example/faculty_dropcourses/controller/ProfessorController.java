package com.example.faculty_dropcourses.controller;

import com.example.faculty_dropcourses.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professors")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    // Endpoint to drop a course
    @DeleteMapping("/{profId}/drop-course/{courseId}")
    public String dropCourse(@PathVariable String profId, @PathVariable String courseId) {
        return professorService.dropCourse(profId, courseId);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}
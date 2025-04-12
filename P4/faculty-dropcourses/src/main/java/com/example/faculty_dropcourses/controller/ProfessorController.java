package com.example.faculty_dropcourses.controller;

import com.example.faculty_dropcourses.model.Courses;
import com.example.faculty_dropcourses.service.ProfessorService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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

    // Endpoint to get courses assigned to a professor
    @GetMapping("/{profId}/courses")
    public List<Courses> getCoursesByProfessor(@PathVariable String profId) {
        return professorService.getCoursesByProfessor(profId);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}
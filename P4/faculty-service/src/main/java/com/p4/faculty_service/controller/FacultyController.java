package com.p4.faculty_service.controller;

import com.p4.faculty_service.model.Course;
import com.p4.faculty_service.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/professors")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    // Endpoint to get assigned courses for a professor
    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getAssignedCourses(@PathVariable String id) {
        List<Course> courses = facultyService.getAssignedCourses(id);
        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(courses);
    }
}

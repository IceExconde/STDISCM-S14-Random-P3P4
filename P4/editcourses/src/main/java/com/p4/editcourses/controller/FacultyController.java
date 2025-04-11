package com.p4.editcourses.controller;

import com.p4.editcourses.model.Course;
import com.p4.editcourses.model.Student;
import com.p4.editcourses.CourseRepository;
//import com.p4.editcourses.repository.StudentRepository;
import com.p4.editcourses.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/professors")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private CourseRepository courseRepository;

    // @Autowired
    // private StudentRepository studentRepository;

    // Endpoint to get assigned courses for a professor
    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getAssignedCourses(@PathVariable String id) {
        List<Course> courses = facultyService.getAssignedCourses(id);
        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(courses);
    }

    // // ✅ New endpoint to get enrolled students by courseId
    // @GetMapping("/courses/{courseId}/students")
    // public ResponseEntity<List<Student>> getEnrolledStudents(@PathVariable String courseId) {
    //     Optional<Course> courseOpt = courseRepository.findById(courseId);
    //     if (courseOpt.isEmpty()) {
    //         return ResponseEntity.notFound().build();
    //     }

    //     Course course = courseOpt.get();
    //     List<String> studentIds = course.getStudentIds();
    //     List<Student> students = studentRepository.findByIdIn(studentIds);

    //     return ResponseEntity.ok(students);
    // }
}

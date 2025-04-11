package com.p4.viewenrolledcourses;

import com.p4.viewenrolledcourses.dto.EnrolledCoursesRequest;
import com.p4.viewenrolledcourses.dto.EnrolledCoursesResponse;
import com.p4.viewenrolledcourses.model.Course;
import com.p4.viewenrolledcourses.repository.CourseRepository;
import com.p4.viewenrolledcourses.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.p4.viewenrolledcourses.model.Student;

import java.util.List;

@RestController
@RequestMapping("/api/view-enrolled")
public class EnrolledCoursesController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public ResponseEntity<EnrolledCoursesResponse> viewEnrolledClasses(@RequestBody EnrolledCoursesRequest enrolledCoursesRequest) {
        Logger logger = LoggerFactory.getLogger(EnrolledCoursesController.class);

        Student student = studentRepository.findById(enrolledCoursesRequest.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new EnrolledCoursesResponse("Student not found", null));
        }

        List<String> enrolledCourseIds = student.getEnrolledCourses();

        List<Course> enrolledCourses = courseRepository.findAllById(enrolledCourseIds);

        EnrolledCoursesResponse response = new EnrolledCoursesResponse("Success", enrolledCourses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}

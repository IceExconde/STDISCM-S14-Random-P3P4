package com.p4.enroll;

import com.p4.enroll.dto.EnrollRequest;
import com.p4.enroll.dto.EnrollResponse;
import com.p4.enroll.model.Course;
import com.p4.enroll.model.Student;
import com.p4.enroll.repositories.CourseRepository;
import com.p4.enroll.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/enroll")
public class EnrollController {
    private static final Logger logger = LoggerFactory.getLogger(EnrollController.class);
    private static final int MAX_CLASS_CAPACITY = 45;

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public EnrollController(CourseRepository courseRepository, 
                          StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollResponse> enrollStudentInClass(
            @RequestBody EnrollRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Validate request
        if (request.getStudentId() == null || request.getCourseId() == null) {
            return ResponseEntity.badRequest()
                    .body(new EnrollResponse("Student ID and Course ID are required"));
        }

        // Verify the authenticated student matches the requested student
        String authenticatedStudentId = jwt.getSubject();
        if (!authenticatedStudentId.equals(request.getStudentId())) {
            logger.warn("Student ID mismatch: Authenticated={}, Requested={}", 
                       authenticatedStudentId, request.getStudentId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new EnrollResponse("You can only enroll yourself"));
        }

        try {
            // Find student and course
            Optional<Student> studentOptional = studentRepository.findById(request.getStudentId());
            Optional<Course> courseOptional = courseRepository.findById(request.getCourseId());

            if (studentOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new EnrollResponse("Student not found"));
            }
            if (courseOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new EnrollResponse("Course not found"));
            }

            Student student = studentOptional.get();
            Course course = courseOptional.get();

            logger.info("Enrollment attempt - Student: {}, Course: {}", 
                        student.getName(), course.getCourse());

            // Validate enrollment conditions
            if (student.getEnrolledCourses().contains(request.getCourseId())) {
                return ResponseEntity.badRequest()
                        .body(new EnrollResponse("Already enrolled in this course"));
            }

            if (course.getCount() >= MAX_CLASS_CAPACITY) {
                return ResponseEntity.badRequest()
                        .body(new EnrollResponse("Course has reached maximum capacity"));
            }

            // Process enrollment
            student.getEnrolledCourses().add(request.getCourseId());
            course.getStudentIds().add(request.getStudentId());
            course.setCount(course.getCount() + 1);

            studentRepository.save(student);
            courseRepository.save(course);

            logger.info("Enrollment successful - Student: {}, Course: {}", 
                       student.getName(), course.getCourse());

            return ResponseEntity.ok(new EnrollResponse("Enrollment successful"));

        } catch (Exception e) {
            logger.error("Enrollment failed for student {} in course {}: {}", 
                        request.getStudentId(), request.getCourseId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EnrollResponse("Enrollment processing failed"));
        }
    }
}
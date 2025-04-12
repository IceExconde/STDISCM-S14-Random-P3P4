package com.p4.createcourse;

import com.p4.createcourse.dto.CreateCourseRequest;
import com.p4.createcourse.dto.CreateCourseResponse;
import com.p4.createcourse.model.Course;
import com.p4.createcourse.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Random;

@RestController
@RequestMapping("/api/create-course")
public class CreateCourseController {
    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public ResponseEntity<CreateCourseResponse> createCourse(@RequestBody CreateCourseRequest request) {
        try {
            // Basic validation (optional)
            if (request.getCourse() == null || request.getSection() == null || request.getDays() == null ||
                    request.getTime() == null || request.getRoom() == null || request.getFacultyId() == null) {
                return ResponseEntity.badRequest()
                        .body(new CreateCourseResponse("Missing required fields in request"));
            }

            // Generate unique 4-digit class number
            String generatedClassNbr = generateUniqueClassNbr();

            // Create the Course object
            Course newCourse = new Course(
                    generatedClassNbr,
                    request.getCourse(),
                    request.getSection(),
                    request.getDays(),
                    request.getTime(),
                    request.getRoom(),
                    request.getFacultyId()
            );

            // Save to database
            courseRepository.save(newCourse);

            return ResponseEntity.ok()
                    .body(new CreateCourseResponse("Course created successfully"));

        } catch (Exception e) {
            // Catch unexpected errors (e.g. DB failure, null pointer, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CreateCourseResponse("An error occurred while creating the course: " + e.getMessage()));
        }
    }

    private String generateUniqueClassNbr() {
        String classNbr;
        Random random = new Random();

        do {
            int randomNum = 1000 + random.nextInt(9000); // Generates from 1000 to 9999
            classNbr = String.valueOf(randomNum);
        } while (courseRepository.existsById(classNbr));

        return classNbr;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}

package com.p4.viewcourses;

import com.p4.viewcourses.dto.CourseResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/view-courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public ResponseEntity<CourseResponse> getAllCourses() {
        Logger logger = LoggerFactory.getLogger(CourseController.class);

        logger.info("Entering getAllCourses method");

        // Retrieve all courses from the repository
        List<Course> courses = courseRepository.findAll();

        // Log the size of the courses list
        logger.info("Retrieved {} courses from the database", courses != null ? courses.size() : 0);

        // If there are no courses, log and return a no content response
        if (courses == null || courses.isEmpty()) {
            logger.warn("No courses found, returning 204 No Content");
            return ResponseEntity.noContent().build();  // Returns 204 No Content if no courses
        }

        // Wrap the courses in a custom response object
        CourseResponse courseResponse = new CourseResponse("success", "Courses retrieved successfully", courses);

        // Log the successful retrieval of courses
        logger.info("Courses found, returning 200 OK with the list of courses");
        return ResponseEntity.ok(courseResponse);
    }

    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }
}
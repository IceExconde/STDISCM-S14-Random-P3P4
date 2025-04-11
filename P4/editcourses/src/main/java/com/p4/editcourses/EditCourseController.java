package com.p4.editcourses;

import com.p4.editcourses.dto.EditCourseResponse;
import com.p4.editcourses.CourseRepository;
import com.p4.editcourses.model.Course;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edit-courses")
public class EditCourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public ResponseEntity<EditCourseResponse> getAllCourses() {
        Logger logger = LoggerFactory.getLogger(EditCourseController.class);
        logger.info("Entering getAllCourses method");

        List<Course> courses = courseRepository.findAll();
        logger.info("Retrieved {} courses from the database", courses != null ? courses.size() : 0);

        if (courses == null || courses.isEmpty()) {
            logger.warn("No courses found, returning 204 No Content");
            return ResponseEntity.noContent().build();
        }

        EditCourseResponse courseResponse = new EditCourseResponse("success", "Courses retrieved successfully", courses);
        logger.info("Courses found, returning 200 OK with the list of courses");
        return ResponseEntity.ok(courseResponse);
    }

    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    // Add this new endpoint for updating a course
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @RequestBody Course courseDetails) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setCourse(courseDetails.getCourse());
                    course.setSection(courseDetails.getSection());
                    course.setDays(courseDetails.getDays());
                    course.setTime(courseDetails.getTime());
                    course.setRoom(courseDetails.getRoom());
                    course.setFacultyId(courseDetails.getFacultyId());
                    Course updatedCourse = courseRepository.save(course);
                    return ResponseEntity.ok(updatedCourse);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<EditCourseResponse> getCoursesByProfessor(@PathVariable String professorId) {
        Logger logger = LoggerFactory.getLogger(EditCourseController.class);
        logger.info("Fetching courses for professor: {}", professorId);

        List<Course> courses = courseRepository.findByFacultyId(professorId);
        
        if (courses == null || courses.isEmpty()) {
            logger.info("No courses found for professor: {}", professorId);
            return ResponseEntity.noContent().build();
        }

        EditCourseResponse response = new EditCourseResponse("success", "Courses retrieved successfully", courses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running.");
    }
}
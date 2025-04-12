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
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;
 import org.springframework.web.bind.annotation.GetMapping;

 import java.util.Optional;
 
 @RestController
 @RequestMapping("/api/enroll")
 public class EnrollController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<EnrollResponse> enrollStudentinClass(@RequestBody EnrollRequest request) {
        Logger logger = LoggerFactory.getLogger(EnrollController.class);

        logger.info("Enroll method called");

        String studentId = request.getStudentId();
        String courseId = request.getCourseId();
        
        logger.info("student: {}, course: {}", studentId, courseId);

        try {
            //verify both ids exist
            Optional<Student> studentOptional = studentRepository.findById(studentId);
            if (studentOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EnrollResponse("Student not found"));
            }

            Student student = studentOptional.get();
            logger.info("{}", student.getName());


            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if(courseOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EnrollResponse("Course does not exist"));
            }
            Course course = courseOptional.get();

            logger.info("{}", course.getCourse());

            //check if student is already enrolled
            if (student.getEnrolledCourses().contains(courseId)) {
                return ResponseEntity.badRequest().body(
                        new EnrollResponse("Student is already enrolled in this class.")
                );
            }

            if(course.getCount() >= 45) {
                return ResponseEntity.badRequest().body(
                        new EnrollResponse("Class is full")
                );
            }

            student.getEnrolledCourses().add(courseId);
            studentRepository.save(student);

            course.getStudentIds().add(studentId);
            course.setCount(course.getCount() + 1);
            courseRepository.save(course);

            return ResponseEntity.ok(new EnrollResponse("Enrollment successful."));
        } catch(Exception e) {
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new EnrollResponse("An error occurred during enrollment.")
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Enroll service is up and running.");
    }
 }
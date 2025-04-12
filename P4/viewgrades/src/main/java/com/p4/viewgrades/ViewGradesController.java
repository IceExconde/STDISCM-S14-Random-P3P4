package com.p4.viewgrades;

import com.p4.viewgrades.dto.ModifiedGradeDTO;
import com.p4.viewgrades.dto.ViewGradesRequest;
import com.p4.viewgrades.dto.ViewGradesResponse;
import com.p4.viewgrades.models.Course;
import com.p4.viewgrades.models.Grade;
import com.p4.viewgrades.repositories.CourseRepository;
import com.p4.viewgrades.repositories.GradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/view-grades")
public class ViewGradesController {

    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public ResponseEntity<ViewGradesResponse> getAllGradesbyStudent(@RequestBody ViewGradesRequest viewGradesRequest) {
        Logger logger = LoggerFactory.getLogger(ViewGradesController.class);
        //add jwt validation

        String studentId = viewGradesRequest.getStudentId();

        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        if (grades.isEmpty()) {
            logger.info("No grades found for studentId={}", studentId);
            return ResponseEntity.noContent().build();
        }

        List<ModifiedGradeDTO> gradeDTOs = grades.stream().map(grade -> {
            Optional<Course> courseOpt = courseRepository.findById(grade.getCourseId());
            String courseName = courseOpt.map(Course::getCourse).orElse("Unknown Course");

            return new ModifiedGradeDTO(
                    grade.getCourseId(),
                    courseName,
                    grade.getGrade()
            );
        }).toList();

        ViewGradesResponse response = new ViewGradesResponse();
        response.setGrades(gradeDTOs);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("View Grades service is up and running.");
    }
}

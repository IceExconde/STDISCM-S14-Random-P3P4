package com.p4.editcourses.service;

import com.p4.editcourses.model.Professor;
import com.p4.editcourses.model.Course;
import com.p4.editcourses.ProfessorRepository;
import com.p4.editcourses.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Get assigned courses for a professor
    public List<Course> getAssignedCourses(String professorId) {
        // Fetch professor by id
        Professor professor = professorRepository.findById(professorId).orElse(null);

        // If professor doesn't exist, return null or handle the error accordingly
        if (professor == null) {
            return null;
        }

        // Get the courseIds for the professor
        List<String> courseIds = professor.getCourseIds();

        // Fetch courses based on courseIds
        return courseRepository.findAll().stream()
                .filter(course -> courseIds.contains(course.getId()))
                .collect(Collectors.toList());
    }
}

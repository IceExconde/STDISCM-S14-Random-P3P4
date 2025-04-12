package com.example.faculty_dropcourses.service;

import com.example.faculty_dropcourses.model.Professor;
import com.example.faculty_dropcourses.model.Courses;
import com.example.faculty_dropcourses.repository.ProfessorRepository;
import com.example.faculty_dropcourses.repository.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CoursesRepository courseRepository;

    // Method to drop a course for a professor
    public String dropCourse(String professorId, String courseId) {
        // Find the professor by ID
        Optional<Professor> professorOpt = professorRepository.findById(professorId);
        if (!professorOpt.isPresent()) {
            return "Professor not found.";
        }

        // Find the course by ID
        Optional<Courses> courseOpt = courseRepository.findById(courseId);
        System.out.println("Looking for course with ID: " + courseId);
        System.out.println("Course exists? " + courseOpt.isPresent());

        if (!courseOpt.isPresent()) {
            return "Course not found.";
        }

        // Get the professor and course objects
        Professor professor = professorOpt.get();
        Courses course = courseOpt.get();

        // Check if the professor is assigned to the course
        if (!course.getFacultyId().equals(professorId)) {
            return "Professor is not assigned to this course.";
        }

        // Remove the course from the professor's list of course IDs
        professor.getCourseIds().remove(courseId);
        professorRepository.save(professor); // Save the updated professor

        // Remove the professor from the course's facultyId
        course.setFacultyId(null);
        courseRepository.save(course); // Save the updated course

        return "Course dropped successfully.";
    }

    public List<Courses> getCoursesByProfessor(String professorId) {
        return courseRepository.findByFacultyId(professorId);
    }
}

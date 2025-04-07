package com.p4.faculty_service.service;

import com.p4.faculty_service.model.Grade;
import com.p4.faculty_service.model.Student;
import com.p4.faculty_service.repository.GradeRepository;
import com.p4.faculty_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Method to add or update a grade
    public Grade addOrUpdateGrade(Grade grade) {
        Optional<Grade> existingGrade = gradeRepository
                .findByStudentIdAndCourseIdAndProfId(grade.getStudentId(), grade.getCourseId(), grade.getProfId());

        if (existingGrade.isPresent()) {
            System.out.println("Existing grade found, updating...");
            Grade toUpdate = existingGrade.get();
            toUpdate.setGrade(grade.getGrade());
            return gradeRepository.save(toUpdate);
        } else {
            System.out.println("No existing grade, inserting new...");
            return gradeRepository.save(grade);
        }
    }

    // Method to get all grades
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // Method to get a grade by studentId and courseId
    public Grade getGradeByStudentAndCourse(String studentId, String courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId).orElse(null);
    }
}

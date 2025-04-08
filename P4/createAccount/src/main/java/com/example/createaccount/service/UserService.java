package com.example.createaccount.service;

import com.example.createaccount.model.User;
import com.example.createaccount.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.createaccount.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.createaccount.dto.RegisterRequest;
import com.example.createaccount.model.Role;
import com.example.createaccount.model.Student;
import com.example.createaccount.model.Professor;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                     StudentRepository studentRepository,
                     ProfessorRepository professorRepository,
                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new RuntimeException("Email already in use");
        }

        // Create and save User
        User user = new User(
            registerRequest.getEmail(),
            passwordEncoder.encode(registerRequest.getPassword()),
            registerRequest.getName(),
            Role.valueOf(registerRequest.getRole().toUpperCase())
        );
        
        User savedUser = userRepository.save(user);

        // Create and save role-specific document
        if (user.getRole() == Role.STUDENT) {
            Student student = new Student(savedUser.getId(), savedUser.getName());
            studentRepository.save(student);
        } else if (user.getRole() == Role.FACULTY) {
            Professor professor = new Professor(savedUser.getId(), savedUser.getName());
            professorRepository.save(professor);
        }

        return savedUser;
    }
}

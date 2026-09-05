package com.rookies6.myspringbootlab.service;

import com.rookies6.myspringbootlab.controller.dto.StudentDTO;
import com.rookies6.myspringbootlab.entity.Student;
import com.rookies6.myspringbootlab.exception.BusinessException;
import com.rookies6.myspringbootlab.exception.ErrorCode;
import com.rookies6.myspringbootlab.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentDTO.Response> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentDTO.Response::fromEntity)
                .toList();
    }

    public StudentDTO.Response getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "id", id));
        return StudentDTO.Response.fromEntity(student);
    }

    public StudentDTO.Response getStudentByStudentNumber(String studentNumber) {
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "student number", studentNumber));
        return StudentDTO.Response.fromEntity(student);
    }

    @Transactional
    public StudentDTO.Response createStudent(StudentDTO.Request request) {
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE,
                    request.getStudentNumber());
        }

        Student student = Student.builder()
                .name(request.getName())
                .studentNumber(request.getStudentNumber())
                .build();

        Student savedStudent = studentRepository.save(student);
        return StudentDTO.Response.fromEntity(savedStudent);
    }

    @Transactional
    public StudentDTO.Response updateStudent(Long id, StudentDTO.Request request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "id", id));

        if (!student.getStudentNumber().equals(request.getStudentNumber()) &&
                studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE,
                    request.getStudentNumber());
        }

        student.setName(request.getName());
        student.setStudentNumber(request.getStudentNumber());

        Student updatedStudent = studentRepository.save(student);
        return StudentDTO.Response.fromEntity(updatedStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Student", "id", id);
        }
        studentRepository.deleteById(id);
    }
}

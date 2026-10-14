package com.rookies6.myspringboot4project.controller.dto;

import com.rookies6.myspringboot4project.entity.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//StudentDTO
public class StudentDTO {

    //요청 데이터를 저장하는 Inner Class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Student name is required")
        @Size(max = 100, message = "Student name cannot exceed 100 characters")
        private String name;

        @NotBlank(message = "Student number is required")
        @Size(max = 20, message = "Student number cannot exceed 20 characters")
        private String studentNumber;
    }

    //응답 데이터를 저장하는 Inner Class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String studentNumber;

        public static Response fromEntity(Student student) {
            return Response.builder()
                    .id(student.getId())
                    .name(student.getName())
                    .studentNumber(student.getStudentNumber())
                    .build();
        }
    }
}
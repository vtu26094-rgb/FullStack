package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;

@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @GetMapping("/students/sort")
    public Page<Student> sortStudents(){

    PageRequest pageable = PageRequest.of(0,5)
            .withSort(org.springframework.data.domain.Sort.by("name"));

    return studentService.getStudents(pageable);

    }
}
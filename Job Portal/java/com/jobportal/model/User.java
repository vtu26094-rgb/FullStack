package com.jobportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Student-specific fields
    @Column(columnDefinition = "TEXT")
    private String skills;

    private String experience;
    private String experienceCompany;
    private String experienceYears;

    private String resumePath;

    private String photoPath;

    private Integer age;

    private String gender;

    private String phone;

    // Academic Details
    private String collegeName;
    private String collegePercentage;
    private String tenthMarks;

    private String twelfthMarks;

    private String ugDetails;

    private String pgDetails;

    // Employer-specific fields
    private String company;

    private String location;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<Job> postedJobs;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Application> applications;

    public enum Role {
        STUDENT, EMPLOYER, ADMIN
    }
}

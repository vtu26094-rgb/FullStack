package com.jobportal.dto;

import lombok.Data;

@Data
public class ProfileDto {

    private String name;

    private String email;

    private String skills;

    private String experience;
    private String experienceCompany;
    private String experienceYears;

    private Integer age;

    private String gender;

    private String phone;

    private String tenthMarks;
    private String collegeName;
    private String collegePercentage;

    private String twelfthMarks;

    private String ugDetails;

    private String pgDetails;

    // Employer fields
    private String company;

    private String location;
}

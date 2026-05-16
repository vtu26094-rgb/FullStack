package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobDto {

    @NotNull(message = "Job title is required")
    @Size(min = 2, max = 150, message = "Title must be between 2 and 150 characters")
    private String title;

    @NotNull(message = "Job description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    private String skillsRequired;

    private String salary;

    private String location;

    private String reqTenthMarks;
    private String reqTwelfthMarks;
    private String reqUg;
    private String reqPg;
    private String reqExperience;
    private String reqExperienceDetails;
    private String applicationQuestions;
}

package com.jobportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 150)
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String skillsRequired;

    private String salary;

    private String location;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Additional requirement fields
    private String reqTenthMarks;
    private String reqTwelfthMarks;
    private String reqUg;
    private String reqPg;
    private String reqExperience; // e.g., "Yes", "No"
    private String reqExperienceDetails;

    @Column(columnDefinition = "TEXT")
    private String applicationQuestions; // Delimited by ||

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Many jobs belong to one employer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;
}

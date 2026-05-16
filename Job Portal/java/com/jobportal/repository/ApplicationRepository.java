package com.jobportal.repository;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // All applications made by a student
    List<Application> findByStudentOrderByAppliedAtDesc(User student);

    // All applications for a specific job
    List<Application> findByJobOrderByAppliedAtDesc(Job job);

    // Check if a student already applied for a job
    boolean existsByStudentAndJob(User student, Job job);

    Optional<Application> findByStudentAndJob(User student, Job job);

    // Count total applications for all jobs of an employer
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer = :employer")
    long countApplicationsByEmployer(@Param("employer") User employer);

    @Query("SELECT a FROM Application a WHERE a.job.employer = :employer AND a.status = :status ORDER BY a.appliedAt DESC")
    List<Application> findByEmployerAndStatus(@Param("employer") User employer, @Param("status") Application.Status status);
}

package com.jobportal.service;

import com.jobportal.dto.JobDto;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Job> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployerOrderByCreatedAtDesc(employer);
    }

    public List<Job> searchJobs(String keyword, String location, String skills) {
        String kw = (keyword != null && keyword.isBlank()) ? null : keyword;
        String loc = (location != null && location.isBlank()) ? null : location;
        String sk = (skills != null && skills.isBlank()) ? null : skills;
        return jobRepository.searchJobs(kw, loc, sk);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    @Transactional
    public Job createJob(JobDto dto, User employer) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkillsRequired(dto.getSkillsRequired());
        job.setSalary(dto.getSalary());
        job.setLocation(dto.getLocation());
        job.setReqTenthMarks(dto.getReqTenthMarks());
        job.setReqTwelfthMarks(dto.getReqTwelfthMarks());
        job.setReqUg(dto.getReqUg());
        job.setReqPg(dto.getReqPg());
        job.setReqExperience(dto.getReqExperience());
        job.setReqExperienceDetails(dto.getReqExperienceDetails());
        job.setApplicationQuestions(dto.getApplicationQuestions());
        job.setEmployer(employer);
        job.setActive(true);
        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long id, JobDto dto, User employer) {
        Job job = getJobById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You are not authorized to edit this job");
        }
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkillsRequired(dto.getSkillsRequired());
        job.setSalary(dto.getSalary());
        job.setLocation(dto.getLocation());
        job.setReqTenthMarks(dto.getReqTenthMarks());
        job.setReqTwelfthMarks(dto.getReqTwelfthMarks());
        job.setReqUg(dto.getReqUg());
        job.setReqPg(dto.getReqPg());
        job.setReqExperience(dto.getReqExperience());
        job.setReqExperienceDetails(dto.getReqExperienceDetails());
        job.setApplicationQuestions(dto.getApplicationQuestions());
        return jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long id, User employer) {
        Job job = getJobById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You are not authorized to delete this job");
        }
        jobRepository.delete(job);
    }

    public long countJobsByEmployer(User employer) {
        return jobRepository.countByEmployer(employer);
    }
}

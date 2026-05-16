package com.jobportal.service;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public List<Application> getApplicationsByStudent(User student) {
        return applicationRepository.findByStudentOrderByAppliedAtDesc(student);
    }

    public List<Application> getApplicationsByJob(Job job) {
        return applicationRepository.findByJobOrderByAppliedAtDesc(job);
    }

    public boolean hasApplied(User student, Job job) {
        return applicationRepository.existsByStudentAndJob(student, job);
    }

    @Transactional
    public Application apply(User student, Job job, String answers, String appliedEmail) {
        if (hasApplied(student, job)) {
            throw new RuntimeException("You have already applied for this job");
        }
        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setAnswers(answers);
        application.setAppliedEmail(appliedEmail);
        application.setStatus(Application.Status.APPLIED);
        application = applicationRepository.save(application);
        
        notificationService.createNotification(job.getEmployer(), 
            "Student " + student.getName() + " has applied for your job: " + job.getTitle());
            
        return application;
    }

    @Transactional
    public void updateStatus(Long applicationId, Application.Status newStatus, User employer) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!app.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Not authorized to update this application");
        }
        app.setStatus(newStatus);
        applicationRepository.save(app);
        
        notificationService.createNotification(app.getStudent(), 
            "Your application for " + app.getJob().getTitle() + " has been marked as " + newStatus.name());

        // Send Email Notification
        if (app.getAppliedEmail() != null && !app.getAppliedEmail().isBlank()) {
            String subject = "Job Application Status Updated: " + app.getJob().getTitle();
            String message = "Dear " + app.getStudent().getName() + ",\n\n" +
                    "Your application for the position of '" + app.getJob().getTitle() + "' at '" + app.getJob().getEmployer().getCompany() + "' has been " + newStatus.name() + ".\n\n" +
                    "Best regards,\nJob Portal Team";
            emailService.sendEmail(app.getAppliedEmail(), subject, message);
        }
    }

    public long countApplicationsByEmployer(User employer) {
        return applicationRepository.countApplicationsByEmployer(employer);
    }

    public List<Application> getApplicationsByEmployerAndStatus(User employer, Application.Status status) {
        return applicationRepository.findByEmployerAndStatus(employer, status);
    }
}

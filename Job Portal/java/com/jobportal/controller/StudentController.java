package com.jobportal.controller;

import com.jobportal.dto.ProfileDto;
import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.ResumeParserService;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final ResumeParserService resumeParserService;

    private static final String UPLOAD_DIR = "uploads/resumes/";
    private static final String PHOTO_DIR = "uploads/photos/";

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userService.findByUsername(userDetails.getUsername());
        List<Application> applications = applicationService.getApplicationsByStudent(student);
        long applied = applications.stream().filter(a -> a.getStatus() == Application.Status.APPLIED).count();
        long shortlisted = applications.stream().filter(a -> a.getStatus() == Application.Status.SHORTLISTED).count();
        long rejected = applications.stream().filter(a -> a.getStatus() == Application.Status.REJECTED).count();
        model.addAttribute("student", student);
        model.addAttribute("totalApplied", applications.size());
        model.addAttribute("shortlisted", shortlisted);
        model.addAttribute("rejected", rejected);
        model.addAttribute("recentApplications", applications.stream().limit(5).toList());
        return "student/dashboard";
    }

    // ---- Profile ----
    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userService.findByUsername(userDetails.getUsername());
        ProfileDto dto = new ProfileDto();
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setSkills(student.getSkills());
        dto.setExperience(student.getExperience());
        dto.setAge(student.getAge());
        dto.setGender(student.getGender());
        dto.setTenthMarks(student.getTenthMarks());
        dto.setTwelfthMarks(student.getTwelfthMarks());
        dto.setUgDetails(student.getUgDetails());
        dto.setPgDetails(student.getPgDetails());
        dto.setCollegeName(student.getCollegeName());
        dto.setCollegePercentage(student.getCollegePercentage());
        dto.setExperienceCompany(student.getExperienceCompany());
        dto.setExperienceYears(student.getExperienceYears());
        model.addAttribute("student", student);
        model.addAttribute("profileDto", dto);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute ProfileDto dto,
                                RedirectAttributes redirectAttributes) {
        userService.updateProfile(userDetails.getUsername(), dto);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/photo")
    public String uploadPhoto(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam("photo") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a photo to upload.");
            return "redirect:/student/profile";
        }
        try {
            File uploadDir = new File(PHOTO_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            String filename = userDetails.getUsername() + "_" + System.currentTimeMillis()
                    + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            Path path = Paths.get(PHOTO_DIR + filename);
            Files.write(path, file.getBytes());
            userService.updatePhotoPath(userDetails.getUsername(), filename);
            redirectAttributes.addFlashAttribute("success", "Profile photo updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload photo: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/resume")
    public String uploadResume(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam("resume") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/student/profile";
        }
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            String filename = userDetails.getUsername() + "_" + System.currentTimeMillis()
                    + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.write(path, file.getBytes());
            
            // Extract data from resume using Affinda
            Map<String, String> extracted = resumeParserService.parseResumeLegacy(file);
            User student = userService.findByUsername(userDetails.getUsername());
            
            // Update student profile with extracted data if they are currently null/empty
            if (student.getSkills() == null || student.getSkills().isBlank()) student.setSkills(extracted.get("skills"));
            if (student.getCollegeName() == null || student.getCollegeName().isBlank()) student.setCollegeName(extracted.get("college"));
            if (student.getCollegePercentage() == null || student.getCollegePercentage().isBlank()) student.setCollegePercentage(extracted.get("collegePercentage"));
            if (student.getPhone() == null || student.getPhone().isBlank()) student.setPhone(extracted.get("phone"));
            if (student.getExperienceYears() == null || student.getExperienceYears().isBlank()) student.setExperienceYears(extracted.get("experienceYears"));
            
            userService.updateResumePath(userDetails.getUsername(), filename);
            // Save the updated student details
            userService.save(student);
            
            redirectAttributes.addFlashAttribute("success", "Resume uploaded and details extracted successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload resume: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    // ---- Browse Jobs ----
    @GetMapping("/jobs")
    public String browseJobs(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String skills,
                             Model model) {
        User student = userService.findByUsername(userDetails.getUsername());
        List<Job> jobs;
        if (keyword != null || location != null || skills != null) {
            jobs = jobService.searchJobs(keyword, location, skills);
        } else {
            jobs = jobService.getAllActiveJobs();
        }
        // Mark which jobs the student already applied for
        model.addAttribute("jobs", jobs);
        model.addAttribute("student", student);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("skills", skills);
        model.addAttribute("applicationService", applicationService);
        return "student/jobs";
    }

    // ---- View Job Details ----
    @GetMapping("/jobs/{id}")
    public String viewJobDetails(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id,
                                 Model model) {
        User student = userService.findByUsername(userDetails.getUsername());
        Job job = jobService.getJobById(id);
        boolean hasApplied = applicationService.hasApplied(student, job);
        model.addAttribute("job", job);
        model.addAttribute("student", student);
        model.addAttribute("hasApplied", hasApplied);
        return "student/job-details";
    }

    // ---- Apply for a Job ----
    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable Long jobId,
                              @RequestParam String answers,
                              @RequestParam String email,
                              RedirectAttributes redirectAttributes) {
        User student = userService.findByUsername(userDetails.getUsername());
        Job job = jobService.getJobById(jobId);
        
        // Simple email validation
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("error", "Invalid email address.");
            return "redirect:/student/jobs/" + jobId;
        }

        if (applicationService.hasApplied(student, job)) {
            redirectAttributes.addFlashAttribute("error", "You have already applied for this job.");
        } else {
            applicationService.apply(student, job, answers, email);
            redirectAttributes.addFlashAttribute("success", "Successfully applied for: " + job.getTitle());
        }
        return "redirect:/student/jobs";
    }

    // ---- My Applications ----
    @GetMapping("/applications")
    public String myApplications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userService.findByUsername(userDetails.getUsername());
        List<Application> applications = applicationService.getApplicationsByStudent(student);
        model.addAttribute("applications", applications);
        model.addAttribute("student", student);
        return "student/my-applications";
    }
}

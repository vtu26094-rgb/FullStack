package com.jobportal.controller;

import com.jobportal.dto.JobDto;
import com.jobportal.dto.ProfileDto;
import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User employer = userService.findByUsername(userDetails.getUsername());
        long jobCount = jobService.countJobsByEmployer(employer);
        long appCount = applicationService.countApplicationsByEmployer(employer);
        List<Job> recentJobs = jobService.getJobsByEmployer(employer).stream().limit(5).toList();
        model.addAttribute("employer", employer);
        model.addAttribute("jobCount", jobCount);
        model.addAttribute("appCount", appCount);
        model.addAttribute("recentJobs", recentJobs);
        return "employer/dashboard";
    }

    // ---- My Jobs ----
    @GetMapping("/my-jobs")
    public String myJobs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User employer = userService.findByUsername(userDetails.getUsername());
        List<Job> jobs = jobService.getJobsByEmployer(employer);
        model.addAttribute("jobs", jobs);
        model.addAttribute("employer", employer);
        return "employer/my-jobs";
    }

    // ---- Post Job ----
    @GetMapping("/post-job")
    public String postJobPage(Model model) {
        model.addAttribute("jobDto", new JobDto());
        return "employer/post-job";
    }

    @PostMapping("/post-job")
    public String postJob(@AuthenticationPrincipal UserDetails userDetails,
                          @Valid @ModelAttribute("jobDto") JobDto dto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "employer/post-job";
        }
        User employer = userService.findByUsername(userDetails.getUsername());
        jobService.createJob(dto, employer);
        redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/employer/my-jobs";
    }

    // ---- Edit Job ----
    @GetMapping("/edit-job/{id}")
    public String editJobPage(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        User employer = userService.findByUsername(userDetails.getUsername());
        Job job = jobService.getJobById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/my-jobs";
        }
        JobDto dto = new JobDto();
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setSkillsRequired(job.getSkillsRequired());
        dto.setSalary(job.getSalary());
        dto.setLocation(job.getLocation());
        dto.setReqTenthMarks(job.getReqTenthMarks());
        dto.setReqTwelfthMarks(job.getReqTwelfthMarks());
        dto.setReqUg(job.getReqUg());
        dto.setReqPg(job.getReqPg());
        dto.setReqExperience(job.getReqExperience());
        dto.setReqExperienceDetails(job.getReqExperienceDetails());
        dto.setApplicationQuestions(job.getApplicationQuestions());
        model.addAttribute("jobDto", dto);
        model.addAttribute("jobId", id);
        return "employer/edit-job";
    }

    @PostMapping("/edit-job/{id}")
    public String editJob(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          @Valid @ModelAttribute("jobDto") JobDto dto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("jobId", id);
            return "employer/edit-job";
        }
        User employer = userService.findByUsername(userDetails.getUsername());
        jobService.updateJob(id, dto, employer);
        redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
        return "redirect:/employer/my-jobs";
    }

    // ---- Delete Job ----
    @PostMapping("/delete-job/{id}")
    public String deleteJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User employer = userService.findByUsername(userDetails.getUsername());
        jobService.deleteJob(id, employer);
        redirectAttributes.addFlashAttribute("success", "Job deleted successfully.");
        return "redirect:/employer/my-jobs";
    }

    // ---- Applicants for a Job ----
    @GetMapping("/applicants/{jobId}")
    public String viewApplicants(@PathVariable Long jobId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        User employer = userService.findByUsername(userDetails.getUsername());
        Job job = jobService.getJobById(jobId);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/my-jobs";
        }
        List<Application> applications = applicationService.getApplicationsByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        model.addAttribute("employer", employer);
        return "employer/applicants";
    }

    // ---- On Hold Candidates ----
    @GetMapping("/on-hold")
    public String viewOnHoldCandidates(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User employer = userService.findByUsername(userDetails.getUsername());
        List<Application> onHoldApplications = applicationService.getApplicationsByEmployerAndStatus(employer, Application.Status.ON_HOLD);
        model.addAttribute("applications", onHoldApplications);
        model.addAttribute("employer", employer);
        return "employer/on-hold";
    }

    // ---- Shortlist / Reject Applicant ----
    @PostMapping("/application/{appId}/status")
    public String updateApplicationStatus(@PathVariable Long appId,
                                          @RequestParam String status,
                                          @RequestParam Long jobId,
                                          @RequestParam(required = false, defaultValue = "false") boolean fromHold,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {
        User employer = userService.findByUsername(userDetails.getUsername());
        Application.Status newStatus = Application.Status.valueOf(status.toUpperCase());
        applicationService.updateStatus(appId, newStatus, employer);
        redirectAttributes.addFlashAttribute("success", "Application status updated to " + status + ".");
        if (fromHold) {
            return "redirect:/employer/on-hold";
        }
        return "redirect:/employer/applicants/" + jobId;
    }

    // ---- Profile ----
    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User employer = userService.findByUsername(userDetails.getUsername());
        ProfileDto dto = new ProfileDto();
        dto.setName(employer.getName());
        dto.setEmail(employer.getEmail());
        dto.setCompany(employer.getCompany());
        dto.setLocation(employer.getLocation());
        model.addAttribute("employer", employer);
        model.addAttribute("profileDto", dto);
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute ProfileDto dto,
                                RedirectAttributes redirectAttributes) {
        userService.updateProfile(userDetails.getUsername(), dto);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/employer/profile";
    }
}

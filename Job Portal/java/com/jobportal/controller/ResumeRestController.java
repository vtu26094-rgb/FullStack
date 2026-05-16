package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.ResumeParserService;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeRestController {

    private final ResumeParserService resumeParserService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndParse(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && 
            !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            return ResponseEntity.badRequest().body("Only PDF and DOCX files are allowed");
        }

        try {
            log.info("User {} uploading resume for parsing", userDetails.getUsername());
            
            // Call service to parse resume using Affinda
            Map<String, Object> extractedData = resumeParserService.parseResumeWithAffinda(file);
            
            // Update user profile
            User user = userService.findByUsername(userDetails.getUsername());
            updateUserProfile(user, extractedData);
            userService.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Resume parsed and profile updated successfully");
            response.put("data", extractedData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to parse resume: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing resume: " + e.getMessage());
        }
    }

    private void updateUserProfile(User user, Map<String, Object> data) {
        if (data.containsKey("name") && data.get("name") != null) user.setName(data.get("name").toString());
        if (data.containsKey("email") && data.get("email") != null) user.setEmail(data.get("email").toString());
        if (data.containsKey("phone") && data.get("phone") != null) user.setPhone(data.get("phone").toString());
        if (data.containsKey("skills") && data.get("skills") != null) user.setSkills(data.get("skills").toString());
        if (data.containsKey("experience") && data.get("experience") != null) user.setExperience(data.get("experience").toString());
        if (data.containsKey("experienceCompany") && data.get("experienceCompany") != null) user.setExperienceCompany(data.get("experienceCompany").toString());
        if (data.containsKey("collegeName") && data.get("collegeName") != null) user.setCollegeName(data.get("collegeName").toString());
        if (data.containsKey("ugDetails") && data.get("ugDetails") != null) user.setUgDetails(data.get("ugDetails").toString());
    }
}

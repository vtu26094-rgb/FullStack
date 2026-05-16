package com.jobportal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeParserService {

    @Value("${affinda.api.key}")
    private String apiKey;

    @Value("${affinda.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> parseResumeWithAffinda(MultipartFile file) throws IOException {
        log.info("Sending resume to Affinda API for parsing: {}", file.getOriginalFilename());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/json");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                return processAffindaResponse(response.getBody());
            } else {
                log.error("Affinda API failed with status: {}", response.getStatusCode());
                throw new RuntimeException("Affinda API failure: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error calling Affinda API: {}", e.getMessage());
            throw new RuntimeException("Failed to parse resume via Affinda: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> processAffindaResponse(Map<String, Object> responseBody) {
        Map<String, Object> extractedData = new HashMap<>();
        if (responseBody == null || !responseBody.containsKey("data")) {
            return extractedData;
        }

        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        
        // Extract Name
        Map<String, Object> nameMap = (Map<String, Object>) data.get("name");
        if (nameMap != null) {
            extractedData.put("name", nameMap.get("raw"));
        }

        // Extract Email
        List<String> emails = (List<String>) data.get("emails");
        if (emails != null && !emails.isEmpty()) {
            extractedData.put("email", emails.get(0));
        }

        // Extract Phone
        List<String> phones = (List<String>) data.get("phoneNumbers");
        if (phones != null && !phones.isEmpty()) {
            extractedData.put("phone", phones.get(0));
        }

        // Extract Skills
        List<Map<String, Object>> skills = (List<Map<String, Object>>) data.get("skills");
        if (skills != null) {
            StringBuilder skillsList = new StringBuilder();
            for (Map<String, Object> skill : skills) {
                if (skillsList.length() > 0) skillsList.append(", ");
                skillsList.append(skill.get("name"));
            }
            extractedData.put("skills", skillsList.toString());
        }

        // Extract Experience (Join multiple with ||)
        List<Map<String, Object>> workExperience = (List<Map<String, Object>>) data.get("workExperience");
        if (workExperience != null && !workExperience.isEmpty()) {
            StringBuilder companies = new StringBuilder();
            StringBuilder titles = new StringBuilder();
            for (Map<String, Object> exp : workExperience) {
                if (companies.length() > 0) companies.append("||");
                if (titles.length() > 0) titles.append("||");
                companies.append(exp.get("organization") != null ? exp.get("organization") : "");
                titles.append(exp.get("jobTitle") != null ? exp.get("jobTitle") : "");
            }
            extractedData.put("experienceCompany", companies.toString());
            extractedData.put("experience", titles.toString());
        }

        // Extract Education (Join multiple with ||)
        List<Map<String, Object>> education = (List<Map<String, Object>>) data.get("education");
        if (education != null && !education.isEmpty()) {
            StringBuilder colleges = new StringBuilder();
            StringBuilder pcts = new StringBuilder();
            for (Map<String, Object> edu : education) {
                if (colleges.length() > 0) colleges.append("||");
                if (pcts.length() > 0) pcts.append("||");
                colleges.append(edu.get("organization") != null ? edu.get("organization") : "");
                // Try to find a grade or just put an empty string for the UI to handle
                pcts.append(""); 
            }
            extractedData.put("collegeName", colleges.toString());
            extractedData.put("collegePercentage", pcts.toString());
        }

        return extractedData;
    }

    // Compatibility method for StudentController
    public Map<String, String> parseResumeLegacy(MultipartFile file) {
        try {
            Map<String, Object> data = parseResumeWithAffinda(file);
            Map<String, String> result = new HashMap<>();
            data.forEach((k, v) -> result.put(k, v != null ? v.toString() : ""));
            // Map keys to match what StudentController expects
            if (data.containsKey("collegeName")) result.put("college", data.get("collegeName").toString());
            return result;
        } catch (Exception e) {
            log.error("Legacy parsing failed: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}

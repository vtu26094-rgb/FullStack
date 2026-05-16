package com.jobportal.config;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create Admin
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setUsername("admin");
            admin.setEmail("admin@jobportal.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }

        // Create sample Employer
        User employer;
        if (!userRepository.existsByUsername("veltech")) {
            User emp = new User();
            emp.setName("John Smith");
            emp.setUsername("veltech");
            emp.setEmail("hr@veltech.com");
            emp.setPassword(passwordEncoder.encode("employer123"));
            emp.setRole(User.Role.EMPLOYER);
            emp.setCompany("Veltech Solutions");
            emp.setLocation("Bangalore");
            employer = userRepository.save(emp);

            // Create sample Jobs
            Job job1 = new Job();
            job1.setTitle("Java Backend Developer");
            job1.setDescription("We are looking for an experienced Java developer with Spring Boot knowledge to build scalable REST APIs.");
            job1.setSkillsRequired("Java, Spring Boot, REST API, MySQL");
            job1.setSalary("6-10 LPA");
            job1.setLocation("Bangalore");
            job1.setEmployer(employer);
            jobRepository.save(job1);

            Job job2 = new Job();
            job2.setTitle("Frontend Developer");
            job2.setDescription("Looking for a creative frontend developer with strong CSS and JavaScript skills.");
            job2.setSkillsRequired("HTML, CSS, JavaScript, ReactJS");
            job2.setSalary("4-8 LPA");
            job2.setLocation("Remote");
            job2.setEmployer(employer);
            jobRepository.save(job2);

            Job job3 = new Job();
            job3.setTitle("Full Stack Developer");
            job3.setDescription("Exciting opportunity to work on full stack development projects using modern technologies.");
            job3.setSkillsRequired("Java, Spring Boot, ReactJS, MySQL");
            job3.setSalary("8-14 LPA");
            job3.setLocation("Hyderabad");
            job3.setEmployer(employer);
            jobRepository.save(job3);
        }

        // Create sample Student
        if (!userRepository.existsByUsername("student1")) {
            User student = new User();
            student.setName("Sukumar");
            student.setUsername("student1");
            student.setEmail("sukumar@example.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole(User.Role.STUDENT);
            student.setSkills("Java, Spring Boot, MySQL");
            student.setExperience("1 year");
            userRepository.save(student);
        }

        System.out.println("======================================");
        System.out.println(" Job Portal - Demo Data Loaded!");
        System.out.println(" Admin:    admin / admin123");
        System.out.println(" Employer: veltech / employer123");
        System.out.println(" Student:  student1 / student123");
        System.out.println("======================================");
    }
}

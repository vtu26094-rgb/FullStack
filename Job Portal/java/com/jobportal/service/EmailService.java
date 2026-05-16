package com.jobportal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
public class EmailService {

    private final Optional<JavaMailSender> mailSender;
    
    public EmailService(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        log.info("Sending email to {}: {}", to, subject);
        try {
            if (mailSender.isPresent()) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("jobportal@example.com");
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                mailSender.get().send(message);
                log.info("Email sent successfully to {}", to);
            } else {
                log.warn("JavaMailSender not configured. Email not sent.");
                // Fallback: Just log it
                System.out.println("---- MOCK EMAIL ----");
                System.out.println("To: " + to);
                System.out.println("Subject: " + subject);
                System.out.println("Body: " + text);
                System.out.println("--------------------");
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}. Note: SMTP might not be configured.", to, e.getMessage());
            // Fallback: Just log it
            System.out.println("---- MOCK EMAIL ----");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + text);
            System.out.println("--------------------");
        }
    }
}

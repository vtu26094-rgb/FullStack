package com.library.notificationservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    @PostMapping
    public String sendNotification(@RequestBody String message) {
        return "Notification Sent: " + message;
    }
}
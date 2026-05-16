package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.NotificationService;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;

    @ModelAttribute("unreadNotificationsCount")
    public long addUnreadNotificationsCount(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                User user = userService.findByUsername(authentication.getName());
                return notificationService.getUnreadCount(user);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
}

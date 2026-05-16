package com.jobportal.controller;

import com.jobportal.model.Notification;
import com.jobportal.model.User;
import com.jobportal.service.NotificationService;
import com.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public String viewNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Notification> notifications = notificationService.getUserNotifications(user);
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        
        // Return based on role
        if (user.getRole() == User.Role.STUDENT) {
            model.addAttribute("student", user);
            return "student/notifications";
        } else if (user.getRole() == User.Role.EMPLOYER) {
            model.addAttribute("employer", user);
            return "employer/notifications";
        }
        return "redirect:/";
    }

    @PostMapping("/mark-read")
    public String markAllAsRead(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        notificationService.markAllAsRead(user);
        redirectAttributes.addFlashAttribute("success", "All notifications marked as read.");
        return "redirect:/notifications";
    }

    @GetMapping("/api/unread-count")
    @ResponseBody
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Map.of("count", 0L);
        }
        User user = userService.findByUsername(userDetails.getUsername());
        long count = notificationService.getUnreadCount(user);
        return Map.of("count", count);
    }
}

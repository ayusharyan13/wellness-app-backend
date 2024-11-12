package com.ayush.blog.appointment.controller;

import com.ayush.blog.appointment.Notification.NotificationProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationProducer notificationProducer;

    @PostMapping("/sendNotification")
    public String sendNotification(@RequestParam String email, @RequestParam String status) {
        try {
            String message = email + "," + status;
            notificationProducer.sendMessage(message);
            return "Notification sent successfully!";
        } catch (Exception e) {
            return "Error sending notification: " + e.getMessage();
        }
    }
}

package com.ayush.blog.appointment.Notification;
import com.ayush.blog.appointment.entity.Appointment;
import com.ayush.blog.appointment.service.AppointmentService;
import com.ayush.blog.appointment.service.EmailService;
import com.ayush.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "emailNotification", groupId = "my-group")
    public void consume(String message) {
        try {
            System.out.println("Received message: " + message);
            // Extract email and status from message
            String[] parts = message.split(":");
            String email = parts[0].replace("Status update for user ", "").trim();
            String status = parts[1].trim();

            // Send email
            emailService.sendStatusUpdateEmail(email, status);
            System.out.println("Email sent to: " + email + " with status: " + status);
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

}

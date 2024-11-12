package com.ayush.blog.appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendStatusUpdateEmail(String email, String status) {
        // Create a SimpleMailMessage object
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ayusharyan1309@gmail.com");  // Replace with your email
        message.setTo(email);
        message.setSubject("Appointment Status Update");
        message.setText("Dear user, your appointment status has been updated to: " + status);

        try {
            // Send the email
            mailSender.send(message);
            System.out.println("Email sent successfully to " + email + " with status: " + status);
        } catch (Exception e) {
            System.err.println("Error sending email to " + email + ": " + e.getMessage());
        }
    }
}

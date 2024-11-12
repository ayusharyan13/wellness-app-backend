//package com.ayush.blog.appointment.Notification;
//
//import com.ayush.blog.appointment.entity.Appointment;
//import org.springframework.mail.SimpleMailMessage;
//
//public class MailNotification {
//
//    public void sendEmailNotification(String toEmail, Appointment appointment) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Appointment Status Updated");
//        message.setText("Your appointment scheduled for " + appointment.getSlotStartTime() + " has been updated to 'SCHEDULED'.");
//        mailSender.send(message);
//    }
//}

//package com.ayush.blog.appointment.controller;
//
//import com.ayush.blog.appointment.entity.Appointment;
//import com.ayush.blog.appointment.service.AppointmentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/appointments")
//public class AdminAppointmentController {
//
//    @Autowired
//    private AppointmentService appointmentService;
//
//    // Get all appointments for admin, filtered by status (if status provided)
//    @GetMapping("/admin")
//    public ResponseEntity<List<Appointment>> getAllAppointmentsForAdmin(@RequestParam(required = false) String status) {
//        List<Appointment> appointments;
//        if (status != null) {
//            appointments = appointmentService.findAppointmentsByStatus(status);
//        } else {
//            appointments = appointmentService.findAllAppointments();
//        }
//        return ResponseEntity.ok(appointments);
//    }
//
//    // Update the status of an appointment
//    @PutMapping("/admin/update-status/{appointmentId}")
//    public ResponseEntity<Appointment> updateAppointmentStatus(
//            @PathVariable String appointmentId,
//            @RequestParam String status) {
//
//        Appointment updatedAppointment = appointmentService.updateAppointmentStatus(appointmentId, status);
//        return ResponseEntity.ok(updatedAppointment);
//    }
//}
//

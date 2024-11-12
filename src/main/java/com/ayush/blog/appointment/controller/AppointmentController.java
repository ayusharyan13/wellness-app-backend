package com.ayush.blog.appointment.controller;
import com.ayush.blog.appointment.DTO.AppointmentDTO;
import com.ayush.blog.appointment.DTO.AppointmentRequest;
import com.ayush.blog.appointment.Notification.NotificationProducer;
import com.ayush.blog.appointment.entity.Appointment;
import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.SlotRepo;
import com.ayush.blog.appointment.service.AppointmentService;
import com.ayush.blog.appointment.service.ConsultantService;
import com.ayush.blog.appointment.service.UserService;
import com.ayush.blog.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SlotRepo slotRepo;
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);


    @PostMapping("/book")
    public ResponseEntity<String> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        logger.info("Received appointment request: {}", appointmentRequest);

        try {
            // Extract date and time from the request
            LocalDate requestDate = appointmentRequest.getDate();
            LocalTime requestSlotTime = appointmentRequest.getSlotTime();
            logger.info("Requested date: {}, slot time: {}", requestDate, requestSlotTime);

            // Fetch available slots from Redis using the date as the key
            String redisKey = "slots:" + requestDate;
            logger.info("Fetching available slots for dateKey: {}", redisKey);

            Set<String> availableSlots = appointmentService.getAvailableSlotsFromRedis(redisKey);
            if (availableSlots == null || !availableSlots.contains(requestSlotTime.toString())) {
                logger.warn("Slot with start time {} not found in Redis.", requestSlotTime);
                return ResponseEntity.badRequest().body("Slot not found");
            }

            // Fetch the corresponding slot from MySQL
            Slot slot = slotRepo.findByStartTime(LocalDateTime.of(requestDate, requestSlotTime))
                    .orElseThrow(() -> new IllegalArgumentException("Slot not found in MySQL"));

            // Check if the slot is already booked
            boolean isSlotBooked = appointmentService.existsBySlotStartTime(LocalDateTime.of(requestDate, requestSlotTime));
            if (isSlotBooked) {
                logger.warn("Requested slotStartTime {} is already booked.", requestSlotTime);
                return ResponseEntity.badRequest().body("Requested slot is already booked");
            }

            // Fetch the user and consultant
            User user = userService.findById(appointmentRequest.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Consultant consultant = consultantService.findById(appointmentRequest.getConsultantId())
                    .orElseThrow(() -> new IllegalArgumentException("Consultant not found"));

            // Book the appointment using the slot fetched from MySQL
            appointmentService.bookAppointment(user, consultant, slot);

            return ResponseEntity.ok("Appointment booked successfully!");

        } catch (Exception e) {
            logger.error("Error while booking appointment: ", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }


    @GetMapping("/upcoming/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointments(@PathVariable Long userId) {
        List<Appointment> appointments = appointmentService.findUpcomingAppointments(userId);

        // Filter to show only appointments with 'SCHEDULED' status
        List<AppointmentDTO> scheduledAppointments = appointments.stream()
                .filter(appointment -> "SCHEDULED".equals(appointment.getStatus()))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(scheduledAppointments);
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getPendingAppointments(@PathVariable Long userId) {
        List<Appointment> appointments = appointmentService.findUpcomingAppointments(userId);

        // Filter to show only appointments with 'PENDING' status
        List<AppointmentDTO> pendingAppointments = appointments.stream()
                .filter(appointment -> "PENDING".equals(appointment.getStatus()))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pendingAppointments);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam String date) {
        Jedis jedis = new Jedis(redisHost, redisPort);
        jedis.auth(redisPassword);

        String slotKey = "slots:" + date; // Ensure date is formatted correctly
        Set<String> availableSlots = jedis.smembers(slotKey);
        jedis.close();

        if (availableSlots == null || availableSlots.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(new ArrayList<>(availableSlots));
    }

    @PutMapping("/admin/appointments/{appointmentId}/status")
    public ResponseEntity<String> changeAppointmentStatus(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);

        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }

        // Change status to 'SCHEDULED'
        appointment.setStatus("SCHEDULED");

        // Save the updated appointment
        appointmentService.saveAppointment(appointment);

        // Send notification to Kafka with email and status
        try {
            notificationProducer.sendMessage("Status update for user " + appointment.getUser().getEmail() + ": " + appointment.getStatus());
        } catch (Exception e) {
            System.out.println("Failed to send notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Appointment status updated, but notification failed");
        }

        return ResponseEntity.ok("Appointment status updated to SCHEDULED");
    }
}






    /*

    // PREV ONE THAT WAS WORKING

    @GetMapping("/completed/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getCompletedAppointments(@PathVariable Long userId) {
        List<Appointment> appointments = appointmentService.findCompletedAppointments(userId);

        // Convert the list of appointments to AppointmentDTOs
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .toList();

        return ResponseEntity.ok(appointmentDTOs);
    }

    @PutMapping("/admin/appointments/{appointmentId}/status")
    public ResponseEntity<String> changeAppointmentStatus(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);

        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }

        // Change status to 'SCHEDULED'
        appointment.setStatus("SCHEDULED");

        // Save the updated appointment
        appointmentService.saveAppointment(appointment);



      //  Send notifications (via Email and WhatsApp)  (synchronously taking a longer time :
        appointmentService.sendStatusUpdateEmail(appointment.getUser().getEmail(), appointment.getStatus());


//        // Now send notification asynchronously
//        notificationProducer.sendNotification("Status changed to " + appointment.getStatus());
        return ResponseEntity.ok("Appointment status updated to SCHEDULED");
    }

     */



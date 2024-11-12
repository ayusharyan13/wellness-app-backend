package com.ayush.blog.appointment.service;
import com.ayush.blog.appointment.DTO.AppointmentRequest;
import com.ayush.blog.appointment.entity.Appointment;
import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.AppointmentRepo;
import com.ayush.blog.appointment.repository.ConsultantRepo;
import com.ayush.blog.appointment.repository.SlotRepo;
import com.ayush.blog.entity.User;
import com.ayush.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private SlotRepo slotRepo;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisService redisService;
    @Autowired

    private UserService userService;

    private UserRepository userRepository;
    @Autowired
    private ConsultantRepo consultantRepo;
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;
    private final Logger logger = LoggerFactory.getLogger(AppointmentService.class);



    @Autowired
    private SlotService slotService;

    //
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private final String REDIS_LOCK_KEY_PREFIX = "lock:slot:";
    private final String REDIS_SLOT_KEY_PREFIX = "slots:";
  //  @Transactional
//    public Appointment bookAppointment(AppointmentRequest appointmentRequest) {
//        // Fetch User and Slot based on IDs in AppointmentRequest
//        User user = userService.findById(appointmentRequest.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        Slot slot = slotRepo.findById(appointmentRequest.getSlotId())
//                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
//
//        // Check if slot is fully booked
//        if (slot.isFullyBooked()) {
//            throw new IllegalArgumentException("Slot is fully booked.");
//        }
//
//        // Assign the available consultant
//        Consultant assignedConsultant;
//        if (slot.getConsultant1() != null && !isConsultantBooked(slot, slot.getConsultant1())) {
//            assignedConsultant = slot.getConsultant1();
//        } else if (slot.getConsultant2() != null && !isConsultantBooked(slot, slot.getConsultant2())) {
//            assignedConsultant = slot.getConsultant2();
//        } else {
//            throw new IllegalArgumentException("No consultants available in this slot.");
//        }
//
//        // Create and save the appointment
//        Appointment appointment = new Appointment();
//        appointment.setUser(user);
//        appointment.setConsultant(assignedConsultant);
//        appointment.setSlot(slot);
//        appointment.setSlotStartTime(slot.getStartTime());
//        appointment.setAppointmentId(UUID.randomUUID().toString());
//        appointment.setSlotEndTime(slot.getEndTime());
//        appointment.setPhoneNumber("838358y484");
//        appointment.setServiceType("Wellness");
//        appointment.setStatus("PENDING");
//
//        appointmentRepo.save(appointment);
//
//        // Update slot booking status if fully booked
//        if (isSlotFullyBooked(slot)) {
//            slot.setFullyBooked(true);
//            slotRepo.save(slot);
//        }
//
//        return appointment;
//    }

    @Transactional
    public Appointment bookAppointment(AppointmentRequest appointmentRequest) {
        User user = userService.findById(appointmentRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Convert slotTime (LocalTime) and date (LocalDate) into LocalDateTime
        LocalDateTime slotStartTime = LocalDateTime.of(appointmentRequest.getDate(), appointmentRequest.getSlotTime());

        // Call findAvailableSlot with LocalDateTime
        Slot slot = slotRepo.findAvailableSlot(appointmentRequest.getDate(), slotStartTime)
                .orElseThrow(() -> new IllegalArgumentException("No slot available at the given time"));

        // Now find an available consultant
        Consultant assignedConsultant = findAvailableConsultantForSlot(slot);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setConsultant(assignedConsultant);
        appointment.setSlot(slot);
        appointment.setSlotStartTime(slot.getStartTime());
        appointment.setSlotEndTime(slot.getEndTime());

        appointment.setServiceType("General consult");
        appointment.setAppointmentId(UUID.randomUUID().toString());
        appointment.setStatus("PENDING");
        appointment.setPhoneNumber("83588544y5");

        appointmentRepo.save(appointment);

        return appointment;
    }

    private Consultant findAvailableConsultantForSlot(Slot slot) {
        // Check consultant availability
        System.out.println("Slot details: " + slot); // Log the slot
        if (slot.getConsultant1() != null) {
            System.out.println("Consultant 1: " + slot.getConsultant1().getName());
        }
        if (slot.getConsultant2() != null) {
            System.out.println("Consultant 2: " + slot.getConsultant2().getName());
        }

        // Check if consultant1 is available
        if (slot.getConsultant1() != null && !isConsultantBooked(slot, slot.getConsultant1())) {
            return slot.getConsultant1();
        }
        // Check if consultant2 is available
        else if (slot.getConsultant2() != null && !isConsultantBooked(slot, slot.getConsultant2())) {
            return slot.getConsultant2();
        } else {
            throw new IllegalArgumentException("No consultants available in this slot.");
        }
    }

    private boolean isConsultantBooked(Slot slot, Consultant consultant) {
        return appointmentRepo.findAppointmentsBySlotAndConsultant(slot.getStartTime(), consultant).size() > 0;
    }



//    @Transactional
//    public void cancelAppointment(Long appointmentId) {
//        Appointment appointment = appointmentRepo.findById(appointmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
//
//        // Restore the slot in Redis
//        Slot slot = slotRepo.findByStartTime(appointment.getSlotStartTime())
//                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
//        slotService.restoreSlotInRedis(slot);
//
//        // Remove appointment
//        appointmentRepo.delete(appointment);
//    }


    public void sendStatusUpdateEmail(String toEmail, String appointmentStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Appointment Status Update with WellNess Centre");
        message.setText("Your appointment status has been updated to: " + appointmentStatus);
        mailSender.send(message);
    }


    public List<Appointment> findUpcomingAppointments(Long userId) {
        return appointmentRepo.findUpcomingAppointments(userId, LocalDateTime.now());
    }

    public List<Appointment> findCompletedAppointments(Long userId) {
        return appointmentRepo.findCompletedAppointments(userId);
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepo.save(appointment);
    }
    public Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
}
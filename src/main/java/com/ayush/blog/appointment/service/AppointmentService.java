package com.ayush.blog.appointment.service;
import com.ayush.blog.appointment.entity.Appointment;
import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.AppointmentRepo;
import com.ayush.blog.appointment.repository.ConsultantRepo;
import com.ayush.blog.appointment.repository.SlotRepo;
import com.ayush.blog.entity.User;
import jakarta.annotation.PostConstruct;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private SlotRepo slotRepository;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ConsultantRepo consultantRepo;
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;
    private final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    // Method to book an appointment
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private SlotService slotService;

    @Transactional
    public Appointment bookAppointment(User user, Consultant consultant, Slot slot) {
        // Check if the slot is fully booked
        if (slot.isFullyBooked()) {
            throw new IllegalArgumentException("Slot is already fully booked");
        }

        // Proceed with booking the slot
        slot.setFullyBooked(true); // Mark slot as fully booked in MySQL
        slotRepository.save(slot); // Save in MySQL

        // Remove from Redis as it's now booked
        slotService.updateSlotInRedis(slot);

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setSlot(slot);
        appointment.setConsultant(consultant); // Associate consultant with the appointment

        // Set the required fields for the appointment
        appointment.setSlotStartTime(slot.getStartTime()); // Assuming Slot has the start time
        appointment.setSlotEndTime(slot.getEndTime()); // Assuming Slot has the end time
        appointment.setServiceType("General Consultation"); // Set default service type, modify as needed
        appointment.setStatus("pending"); // Set default status as "pending"
        appointment.setPhoneNumber(""); // Set phone number to empty as per your requirement

        // Optionally, generate a unique appointmentId if needed
        appointment.setAppointmentId(UUID.randomUUID().toString()); // Or use a custom ID generation strategy

        appointmentRepo.save(appointment); // Save appointment

        return appointment;
    }


    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Restore the slot in Redis
        Slot slot = slotRepository.findByStartTime(appointment.getSlotStartTime())
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        slotService.restoreSlotInRedis(slot);

        // Remove appointment
        appointmentRepo.delete(appointment);
    }


    public boolean existsBySlotStartTime(LocalDateTime slotStartTime) {
        return appointmentRepo.existsBySlotStartTime(slotStartTime);
    }

    // Get available slots for a specific date
    public Set<String> getAvailableSlotsFromRedis(String dateKey) {
        return redisService.getAvailableSlots(dateKey);
    }





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




  /*

  working booking service
    @Transactional
    public Appointment bookAppointment(User user, Long consultantId, LocalDateTime slotStartTime) {
        // Fetch the consultant and other logic to ensure slots availability
        Consultant consultant = consultantService.findById(consultantId);
        List<Appointment> existingAppointments = appointmentRepo.findAppointmentsBySlotAndConsultant(slotStartTime, consultant);

        if (existingAppointments.size() >= consultant.getMaxAppointmentsPerSlot()) {
            throw new IllegalStateException("Slot fully booked for this consultant.");
        }
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID().toString());
        appointment.setUser(user); // Set the user reference
        appointment.setConsultant(consultant);
        appointment.setServiceType("General Consultation");
        appointment.setSlotStartTime(slotStartTime);
        appointment.setSlotEndTime(slotStartTime.plusMinutes(45)); // Assuming 45 minutes slot
        appointment.setStatus("PENDING");

        // Save the appointment
        return appointmentRepo.save(appointment);
    }
*/

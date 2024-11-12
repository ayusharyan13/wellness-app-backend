package com.ayush.blog.initializer;

import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.SlotRepo;
import com.ayush.blog.appointment.service.ConsultantService;
import com.ayush.blog.appointment.service.SlotInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SlotInitializationService slotInitializationService;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private SlotRepo slotRepo;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Initialize slots for the next 7 days upon application startup
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            initializeSlots(date);
        }
    }

    public void initializeSlots(LocalDate date) {
        // Ensure deletion of existing data before adding new data
        deleteSlotsForDate(date);  // Delete from MySQL
        deleteSlotsFromRedis(date);  // Delete from Redis

        // Now add the new slots for the date
        List<Slot> slots = createSlotsForDate(date);

        // Add new slots to MySQL
        slotRepo.saveAll(slots);

        // Save available slots to Redis
        Set<String> availableSlots = new HashSet<>();
        for (Slot slot : slots) {
            if (!slot.isFullyBooked()) {  // Only add if the slot is not fully booked
                availableSlots.add(slot.getStartTime().toString());
            }
        }

        // Store the available slots in Redis
        String redisKey = "slots:" + date.toString();
        redisTemplate.opsForSet().add(redisKey, availableSlots.toArray(new String[0]));
    }

    public void deleteSlotsForDate(LocalDate date) {
        slotRepo.deleteByDate(date);  // Assumes date is a LocalDate field in Slot entity
    }

    public void deleteSlotsFromRedis(LocalDate date) {
        String redisKey = "slots:" + date.toString();
        redisTemplate.delete(redisKey);
    }

    public List<Slot> createSlotsForDate(LocalDate date) {
        List<Slot> slots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0);  // Starting at 9:00 AM

        // Assign consultants dynamically (ensure these consultants are valid)
        Consultant consultant1 = consultantService.getConsultantById(1L);
        Consultant consultant2 = consultantService.getConsultantById(2L);

        for (int i = 0; i < 8; i++) {  // Create 8 slots for the day
            Slot slot = new Slot();
            slot.setDate(date);
            slot.setStartTime(LocalDateTime.of(date, startTime.plusHours(i)));
            slot.setEndTime(LocalDateTime.of(date, startTime.plusHours(i + 1)));
            slot.setConsultant1(consultant1); // Assign consultant1
            slot.setConsultant2(consultant2); // Assign consultant2
            slot.setFullyBooked(false);
            slots.add(slot);
        }
        return slots;
    }
}

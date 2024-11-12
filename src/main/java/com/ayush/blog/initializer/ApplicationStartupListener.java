package com.ayush.blog.initializer;

import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.SlotRepo;
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
import java.util.List;


@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SlotInitializationService slotInitializationService;
    @Autowired
    private SlotRepo slotRepo;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Initialize slots for the next 7 days upon application startup
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);

            // Ensure deletion of existing data before adding new data
            slotInitializationService.initializeSlots(date);
        }
    }

    public void initializeSlots(LocalDate date) {
        // Delete existing slots in MySQL and Redis for this date
        deleteSlotsForDate(date);  // Delete slots from MySQL
        deleteSlotsFromRedis(date);  // Delete slots from Redis

        // Now add the new slots for the date
        List<Slot> slots = createSlotsForDate(date);
        // Add new slots to MySQL and Redis
        slotRepo.saveAll(slots);  // Save to MySQL
        redisTemplate.opsForValue().set("slots:" + date.toString(), slots.toString());  // Save to Redis
    }

    public void deleteSlotsForDate(LocalDate date) {
        // Deleting slots from MySQL
        String dateKey = date.toString(); // Convert date to string format (e.g., "2024-11-11")
        slotRepo.deleteByDate(dateKey);  // Delete slots by date
    }

    public void deleteSlotsFromRedis(LocalDate date) {
        String redisKey = "slots:" + date.toString();
        redisTemplate.delete(redisKey);
    }

    public List<Slot> createSlotsForDate(LocalDate date) {
        List<Slot> slots = new ArrayList<>();

        // Example: Creating 8 slots for a given date
        LocalTime startTime = LocalTime.of(9, 0);  // Example starting at 9:00 AM
        for (int i = 0; i < 8; i++) {  // Create 8 slots for the day
            Slot slot = new Slot();
            slot.setStartTime(LocalDateTime.of(date, startTime.plusHours(i)));  // Adjust slot time
            slot.setEndTime(LocalDateTime.of(date, startTime.plusHours(i + 1)));  // Slot duration 1 hour
            slot.setBooked(false);

                      // Slot is not booked initially
            slot.setFullyBooked(false);  // Slot is not fully booked initially
            slots.add(slot);
        }
        return slots;
    }
}

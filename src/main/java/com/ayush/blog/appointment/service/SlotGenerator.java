package com.ayush.blog.appointment.service;

import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//@Service
//public class SlotGenerator {
//    @Autowired
//    private ConsultantService consultantService;
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;  // Inject RedisTemplate
//    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//    // Check if the time is within a break period (e.g., lunch or snack breaks)
//    private static boolean isBreakTime(LocalTime time) {
//        return (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(13, 0))) || // Lunch break
//                (time.isAfter(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(16, 30)));  // Snack break
//    }
//
//    // Scheduled task to generate and update slots in Redis
//    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
//    public void updateRedisSlots() {
//        LocalDate today = LocalDate.now();
//        LocalDate newDate = today.plusDays(7); // Generate slots for 7 days in advance
//
//        // Fetch all consultants
//        List<Consultant> consultants = consultantService.getAllConsultants();
//
//        // Generate new slots for the future date
//        List<LocalDateTime[]> newSlots = generateSlotsForDay(newDate, consultants);
//
//        // Store the new slots in Redis
//        redisTemplate.opsForValue().set("slots:" + newDate, newSlots);
//
//        // Optionally, remove outdated slots (e.g., slots older than 7 days)
//        redisTemplate.delete("slots:" + today.minusDays(1));
//    }
//
//
//    // Retrieve available slots from Redis
//    public static List<LocalDateTime[]> getAvailableSlotsFromRedis(Jedis jedis, String redisKey) {
//        List<LocalDateTime[]> availableSlots = new ArrayList<>();
//
//        // Get the list of slots from Redis
//        List<String> slots = jedis.lrange(redisKey, 0, -1);
//
//        // Convert the Redis slot strings into LocalDateTime[] arrays
//        for (String slotString : slots) {
//            try {
//                String[] slotTimes = slotString.split(","); // Assuming stored as "startTime,endTime"
//                LocalDateTime startTime = LocalDateTime.parse(slotTimes[0], formatter);
//                LocalDateTime endTime = LocalDateTime.parse(slotTimes[1], formatter);
//                availableSlots.add(new LocalDateTime[]{startTime, endTime});
//            } catch (Exception e) {
//                e.printStackTrace(); // Handle and log parsing errors
//            }
//        }
//
//        return availableSlots;
//    }
//
//    // Method to generate slots for a specific day
//    public static List<LocalDateTime[]> generateSlotsForDay(LocalDate date, List<Consultant> consultants) {
//        List<LocalDateTime[]> slots = new ArrayList<>();
//
//        // Loop through the list of consultants and generate slots for each
//        for (Consultant consultant : consultants) {
//            // Define working hours (e.g., from 9:00 AM to 5:00 PM)
//            LocalTime startTime = LocalTime.of(9, 0); // 9:00 AM
//            LocalTime endTime = LocalTime.of(17, 0);  // 5:00 PM
//            int slotDurationMinutes = 45; // 30-minute slots
//
//            // Create slots within the specified time range for each consultant
//            LocalTime currentTime = startTime;
//            while (currentTime.isBefore(endTime)) {
//                LocalDateTime startSlot = LocalDateTime.of(date, currentTime);
//                LocalDateTime endSlot = startSlot.plusMinutes(slotDurationMinutes);
//
//                // Add the time slot to the list
//                slots.add(new LocalDateTime[]{startSlot, endSlot});
//
//                // Move to the next slot
//                currentTime = currentTime.plusMinutes(slotDurationMinutes);
//            }
//        }
//        return slots;
//    }
//}


@Service
public class SlotGenerator {
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static List<LocalDateTime[]> generateSlotsForDay(LocalDate date, List<Consultant> consultants) {
        List<LocalDateTime[]> slots = new ArrayList<>();
        for (Consultant consultant : consultants) {
            LocalTime startTime = LocalTime.of(9, 0);  // Start at 9 AM
            LocalTime endTime = LocalTime.of(17, 0);   // End at 5 PM
            int slotDurationMinutes = 45;  // 45-minute slots

            LocalTime currentTime = startTime;
            while (currentTime.plusMinutes(slotDurationMinutes).isBefore(endTime)) {
                LocalDateTime startSlot = LocalDateTime.of(date, currentTime);
                LocalDateTime endSlot = startSlot.plusMinutes(slotDurationMinutes);
                slots.add(new LocalDateTime[]{startSlot, endSlot});
                currentTime = currentTime.plusMinutes(slotDurationMinutes);
            }
        }
        return slots;
    }
}

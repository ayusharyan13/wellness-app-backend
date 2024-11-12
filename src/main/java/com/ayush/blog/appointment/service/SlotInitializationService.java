package com.ayush.blog.appointment.service;

import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.SlotRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SlotInitializationService {

    @Autowired
    private SlotRepo slotRepo;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void initializeSlots(LocalDate date) {
        // Generate slots for the date
        List<Slot> slots = generateSlotsForDate(date);

        // Save slots in MySQL
        for (Slot slot : slots) {
            slotRepo.save(slot);
        }

        // Save slots in Redis (keyed by date)
        String redisKey = "slots:" + date.toString();
        Set<String> redisSlots = slots.stream()
                .map(slot -> slot.getStartTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        redisTemplate.opsForSet().add(redisKey, redisSlots.toArray(new String[0]));
    }

    private List<Slot> generateSlotsForDate(LocalDate date) {
        // Logic to generate slots (for example, every hour from 09:00 to 18:00)
        List<Slot> slots = new ArrayList<>();
        for (int i = 9; i <= 18; i++) {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(i, 0));
            slots.add(new Slot(startTime, false)); // false for not fully booked
        }
        return slots;
    }

}


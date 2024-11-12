package com.ayush.blog.appointment.service;

import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.SlotRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class SlotService {

    @Autowired
    private SlotRepo slotRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String REDIS_SLOT_KEY_PREFIX = "slots:";

    // Store slots in both Redis and MySQL
    @Transactional
    public void initializeSlots(List<Slot> slots) {
        // Store in MySQL
        slotRepository.saveAll(slots);

        // Store in Redis
        for (Slot slot : slots) {
            String dateKey = REDIS_SLOT_KEY_PREFIX + slot.getStartTime().toLocalDate();
            redisTemplate.opsForSet().add(dateKey, slot.getStartTime().toLocalTime().toString());
        }
    }

    // Fetch available slots from Redis
    public Set<String> getAvailableSlotsFromRedis(LocalDate date) {
        String dateKey = REDIS_SLOT_KEY_PREFIX + date;
        return redisTemplate.opsForSet().members(dateKey);
    }

    // Update Redis when a slot is booked
    public void updateSlotInRedis(Slot slot) {
        String dateKey = REDIS_SLOT_KEY_PREFIX + slot.getStartTime().toLocalDate();
        redisTemplate.opsForSet().remove(dateKey, slot.getStartTime().toLocalTime().toString());
    }

    // Re-add slot to Redis (in case of cancelation)
    public void restoreSlotInRedis(Slot slot) {
        String dateKey = REDIS_SLOT_KEY_PREFIX + slot.getStartTime().toLocalDate();
        redisTemplate.opsForSet().add(dateKey, slot.getStartTime().toLocalTime().toString());
    }
}

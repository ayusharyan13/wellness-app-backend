package com.ayush.blog.appointment.service;
import com.ayush.blog.appointment.entity.Slot;
import com.ayush.blog.appointment.repository.SlotRepo;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SlotService slotService;
    @Autowired
    private SlotRepo slotRepo;

    public void saveSlotsForDay(String date, List<String> availableSlots) {
        redisTemplate.opsForSet().remove(date); // Delete existing slots for the day before saving
        redisTemplate.opsForSet().add(date, availableSlots.toArray(new String[0]));
        redisTemplate.expire(date, 7, TimeUnit.DAYS); // Expire keys in 7 days
    }

    public Set<String> getAvailableSlots(String date) {
        return redisTemplate.opsForSet().members(date); // Fetch available slots for the day
    }

    public void bookSlot(String date, String slot) {
        redisTemplate.opsForSet().remove(date, slot); // Remove the booked slot
    }
}

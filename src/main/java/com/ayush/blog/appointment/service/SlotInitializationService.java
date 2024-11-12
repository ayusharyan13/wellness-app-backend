package com.ayush.blog.appointment.service;

import com.ayush.blog.appointment.entity.Consultant;
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
    private ConsultantService consultantService;

    @Transactional
    public void initializeSlots(LocalDate date) {
        // Fetch consultants by their IDs (ensure these IDs are correct or based on your logic)
        Consultant consultant1 = consultantService.getConsultantById(1L);
        Consultant consultant2 = consultantService.getConsultantById(2L);

        // Check if consultants exist, if not throw an exception or handle as needed
        if (consultant1 == null || consultant2 == null) {
            throw new IllegalArgumentException("One or more consultants not found.");
        }

        // Log the consultants to check if they are being retrieved correctly
        System.out.println("Consultant 1: " + consultant1);
        System.out.println("Consultant 2: " + consultant2);

        // Generate slots for the day and assign the consultants
        List<Slot> slots = generateSlotsForDate(date, consultant1, consultant2);

        // Save all generated slots to the repository
        slotRepo.saveAll(slots);
    }

    private List<Slot> generateSlotsForDate(LocalDate date, Consultant consultant1, Consultant consultant2) {
        List<Slot> slots = new ArrayList<>();

        // Generate slots between 9:00 AM and 6:00 PM
        for (int i = 9; i <= 18; i++) {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(i, 0));
            LocalDateTime endTime = startTime.plusHours(1);

            // Assign both consultants to each slot
            Slot slot = new Slot(startTime, endTime, consultant1, consultant2);

            // Add slot to the list
            slots.add(slot);
        }
        return slots;
    }
}

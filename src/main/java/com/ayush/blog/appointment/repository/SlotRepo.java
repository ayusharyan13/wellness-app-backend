package com.ayush.blog.appointment.repository;

import com.ayush.blog.appointment.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepo extends JpaRepository<Slot,Long> {
    // Find slot by its start time
    Optional<Slot> findByStartTime(LocalDateTime startTime);
    // Custom query method to delete slots by date (assuming Slot has a 'date' field)
    void deleteByDate(LocalDate date);


    @Query("SELECT s FROM Slot s WHERE s.date = :date AND s.fullyBooked = false")
    List<Slot> findAvailableSlotsByDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM Slot s WHERE s.date = :date AND s.startTime = :startTime AND s.fullyBooked = false")
    Optional<Slot> findAvailableSlot(LocalDate date, LocalDateTime startTime);


}

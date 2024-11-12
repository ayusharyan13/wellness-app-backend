package com.ayush.blog.appointment.repository;

import com.ayush.blog.appointment.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SlotRepo extends JpaRepository<Slot,Long> {
    // Find slot by its start time
    Optional<Slot> findByStartTime(LocalDateTime startTime);
    // Custom query method to delete slots by date (assuming Slot has a 'date' field)


    @Query("DELETE FROM Slot s WHERE DATE(s.startTime) = :date")
    void deleteByDate(@Param("date") String date);

}

package com.ayush.blog.appointment.repository;
import com.ayush.blog.appointment.entity.Appointment;
import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.slotStartTime > :now")
    List<Appointment> findUpcomingAppointments(Long userId, LocalDateTime now);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.status = 'COMPLETED'")
    List<Appointment> findCompletedAppointments(Long userId);

    boolean existsBySlotStartTime(LocalDateTime slotStartTime);

    @Query("SELECT a FROM Appointment a WHERE a.slotStartTime = :slotStartTime AND a.consultant = :consultant")
    List<Appointment> findAppointmentsBySlotAndConsultant(@Param("slotStartTime") LocalDateTime slotStartTime, @Param("consultant") Consultant consultant);


    List<Appointment> findBySlotAndConsultant(Slot slot, Consultant consultant);
}




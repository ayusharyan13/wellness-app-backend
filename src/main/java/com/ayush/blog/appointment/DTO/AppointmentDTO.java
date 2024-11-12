package com.ayush.blog.appointment.DTO;

import com.ayush.blog.appointment.entity.Appointment;
import jakarta.persistence.Access;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AppointmentDTO {
    // Getters and Setters
    private Long id;
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private String serviceType;
    private String status;

    // Constructor
    public AppointmentDTO(Long id, LocalDateTime slotStartTime, LocalDateTime slotEndTime, String serviceType, String status) {
        this.id = id;
        this.slotStartTime = slotStartTime;
        this.slotEndTime = slotEndTime;
        this.serviceType = serviceType;
        this.status = status;
    }

    // Constructor to convert Appointment entity to DTO
    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.slotStartTime = appointment.getSlotStartTime();
        this.slotEndTime = appointment.getSlotEndTime();
        this.serviceType = appointment.getServiceType();
        this.status = appointment.getStatus();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSlotStartTime(LocalDateTime slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public void setSlotEndTime(LocalDateTime slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.ayush.blog.appointment.DTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentRequest {

    private Long userId;
    private Long consultantId;
    private LocalDate date;      // Format: yyyy-MM-dd
    private LocalTime slotTime;  // Format: HH:mm
}

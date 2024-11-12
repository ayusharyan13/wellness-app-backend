package com.ayush.blog.appointment.entity;
import com.ayush.blog.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "slots",  uniqueConstraints = @UniqueConstraint(columnNames = "startTime"))
public class Slot implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime; // Slot start time
    private LocalDateTime endTime;   // Slot end time

    @ManyToMany
    @JoinTable(
            name = "slot_consultant",
            joinColumns = @JoinColumn(name = "slot_id"),
            inverseJoinColumns = @JoinColumn(name = "consultant_id")
    )
    private List<Consultant> availableConsultants = new ArrayList<>();

    @ManyToMany(mappedBy = "bookedSlots")
    private List<User> bookedUsers = new ArrayList<>();

    private boolean isFullyBooked = false;
    private boolean isBooked = false;  // Add this field for tracking the booking status

    // Custom constructor to initialize specific fields
    public Slot(LocalDateTime startTime, boolean isFullyBooked) {
        this.startTime = startTime;
        this.isFullyBooked = isFullyBooked;
        this.isBooked = false;  // You can default to false if the slot is not yet booked
    }

    // Explicit getter and setter for isBooked
    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    // Explicit getter and setter for isFullyBooked
    public boolean isFullyBooked() {
        return isFullyBooked;
    }

    public void setFullyBooked(boolean isFullyBooked) {
        this.isFullyBooked = isFullyBooked;
    }
}



//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "slots")
//public class Slot implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private LocalDateTime startTime; // Slot start time
//    private LocalDateTime endTime;   // Slot end time
//
//    @ManyToMany
//    @JoinTable(
//            name = "slot_consultant",
//            joinColumns = @JoinColumn(name = "slot_id"),
//            inverseJoinColumns = @JoinColumn(name = "consultant_id")
//    )
//    private List<Consultant> availableConsultants = new ArrayList<>(); // List of consultants available for this slot
//
//    @ManyToMany(mappedBy = "bookedSlots") // Corrected to map by bookedSlots in User
//    private List<User> bookedUsers = new ArrayList<>(); // Track which users have booked this slot
//
//    private boolean isFullyBooked = false; // Flag to track if the slot is fully booked
//}

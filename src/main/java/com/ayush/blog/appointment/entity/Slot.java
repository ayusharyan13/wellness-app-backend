package com.ayush.blog.appointment.entity;
import com.ayush.blog.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant1_id")
    private Consultant consultant1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant2_id")
    private Consultant consultant2;

    private LocalDate date;

    @ManyToMany
    @JoinTable(
            name = "user_slot",
            joinColumns = @JoinColumn(name = "slot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> bookedUsers = new ArrayList<>();

    private boolean fullyBooked = false;

    // Getters and setters
    public Slot(LocalDateTime startTime, LocalDateTime endTime, Consultant consultant1, Consultant consultant2) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.consultant1 = consultant1;
        this.consultant2 = consultant2;
    }
}

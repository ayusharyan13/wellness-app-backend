package com.ayush.blog.appointment.repository;

import com.ayush.blog.appointment.entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultantRepo extends JpaRepository<Consultant, Long> {
}

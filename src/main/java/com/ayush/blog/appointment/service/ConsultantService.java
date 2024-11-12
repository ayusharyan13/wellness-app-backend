package com.ayush.blog.appointment.service;

import com.ayush.blog.appointment.entity.Consultant;
import com.ayush.blog.appointment.repository.ConsultantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultantService {
    @Autowired
    private ConsultantRepo consultantRepo;

    // Return Optional<Consultant> to use orElseThrow later
    public Consultant getConsultantById(Long id) {
        return consultantRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultant with ID " + id + " not found"));
    }
    public List<Consultant> getAllConsultants() {
        return consultantRepo.findAll();
    }
}

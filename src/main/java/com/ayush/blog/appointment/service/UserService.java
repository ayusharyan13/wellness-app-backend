package com.ayush.blog.appointment.service;

import com.ayush.blog.entity.User;
import com.ayush.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    // Find user by ID
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}

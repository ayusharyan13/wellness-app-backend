package com.ayush.blog.service;

import com.ayush.blog.dto.LoginDto;
import com.ayush.blog.dto.RegisterDto;

public interface AuthService {

    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}

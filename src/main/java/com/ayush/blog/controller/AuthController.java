package com.ayush.blog.controller;

import com.ayush.blog.dto.JwtAuthResponse;
import com.ayush.blog.dto.LoginDto;
import com.ayush.blog.dto.RegisterDto;
import com.ayush.blog.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
//
//    @PostMapping(value = {"/login","/signin"})
//    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
//        String token = authService.login(loginDto);
//        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
//        jwtAuthResponse.setAccessToken(token);
//        System.out.println(jwtAuthResponse.getAccessToken());
//        return ResponseEntity.ok(jwtAuthResponse);
//    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto){
        String token = authService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping(value = {"/signup", "/register"})
    public ResponseEntity<String> login(@RequestBody RegisterDto registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.model.User;
import com.example.Hotel_booking.request.AuthRequest;
import com.example.Hotel_booking.request.MeRequest;
import com.example.Hotel_booking.response.AuthResponse;
import com.example.Hotel_booking.response.MessageResponse;
import com.example.Hotel_booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody AuthRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
    @PostMapping("/me")
    public User login(@RequestBody MeRequest request) {
        return authService.me(request.getToken());
    }

}

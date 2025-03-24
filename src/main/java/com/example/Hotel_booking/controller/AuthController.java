package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.model.User;
import com.example.Hotel_booking.request.AuthRequest;
import com.example.Hotel_booking.request.ForgotPasswordRequest;
import com.example.Hotel_booking.request.MeRequest;
import com.example.Hotel_booking.request.UpdateUserRequest;
import com.example.Hotel_booking.request.RequestOtpRequest;
import com.example.Hotel_booking.request.VerifyOtpRequest;
import com.example.Hotel_booking.request.ResetPasswordRequest;
import com.example.Hotel_booking.request.ChangePasswordRequest;
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
    @PostMapping("/update-profile")
    public ResponseEntity<MessageResponse> updateProfile(@RequestBody UpdateUserRequest request) {
        try {
            String message = authService.updateUserProfile(request);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String message = authService.forgotPassword(request);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<MessageResponse> requestOtp(@RequestBody RequestOtpRequest request) {
        String message = authService.requestOtp(request);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        String message = authService.verifyOtp(request);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        String message = authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Lấy token từ header
            System.out.println("vao");
            String token = authHeader.substring(7); // Bỏ "Bearer " ở đầu
            System.out.println(token);
            String message = authService.changePassword(token, request);
            return ResponseEntity.ok(new MessageResponse(message));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new MessageResponse(e.getMessage()));
        }
    }
}

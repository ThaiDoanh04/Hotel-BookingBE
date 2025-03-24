package com.example.Hotel_booking.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email(message = "Vui lòng cung cấp địa chỉ email hợp lệ")
    @NotBlank(message = "Email là bắt buộc")
    private String email;

    @NotBlank(message = "First name là bắt buộc")
    private String firstName;

    @NotBlank(message = "Last name là bắt buộc")
    private String lastName;

    @NotBlank(message = "Phone number là bắt buộc")
    private String phoneNumber;
    
} 
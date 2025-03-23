package com.example.Hotel_booking.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestOtpRequest {
    @Email(message = "Vui lòng cung cấp địa chỉ email hợp lệ")
    @NotBlank(message = "Email là bắt buộc")
    private String email;
} 
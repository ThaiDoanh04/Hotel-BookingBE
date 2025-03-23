package com.example.Hotel_booking.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Email(message = "Vui lòng cung cấp địa chỉ email hợp lệ")
    @NotBlank(message = "Email là bắt buộc")
    private String email;
    
    @NotBlank(message = "Mã OTP là bắt buộc")
    private String otpCode;
    
    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String newPassword;
    
    @NotBlank(message = "Xác nhận mật khẩu là bắt buộc")
    private String confirmPassword;
} 
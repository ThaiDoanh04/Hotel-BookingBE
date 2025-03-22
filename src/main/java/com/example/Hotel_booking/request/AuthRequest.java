package com.example.Hotel_booking.request;


import com.example.Hotel_booking.model.Role;
import lombok.*;
import jakarta.validation.constraints.*;


@Data
public class AuthRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private Role role = Role.USER; // Default role

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isUser() {
        return Role.USER.equals(this.role);
    }
}

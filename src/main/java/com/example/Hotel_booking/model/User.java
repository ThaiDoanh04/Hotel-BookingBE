package com.example.Hotel_booking.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL) 
    private Role role = Role.USER; 
    
    // Thêm các trường để quản lý OTP
    private String otpCode;
    private LocalDateTime otpExpiration;
}

package com.example.Hotel_booking.repository;

import com.example.Hotel_booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndOtpCode(String email, String otpCode);
}

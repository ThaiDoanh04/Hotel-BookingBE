package com.example.Hotel_booking.service;




import com.example.Hotel_booking.model.User;
import com.example.Hotel_booking.repository.UserRepository;
import com.example.Hotel_booking.request.AuthRequest;
import com.example.Hotel_booking.response.AuthResponse;
import com.example.Hotel_booking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User me(String token){
        String email=jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public String register(AuthRequest request) {
        // Validate if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);
        return "User registered successfully!";
    }

    public AuthResponse login(AuthRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent() &&
                passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            String token = jwtUtil.generateToken(request.getEmail());
            return new AuthResponse(token);
        } else {
            throw new RuntimeException("Invalid credentials!");
        }
    }
}

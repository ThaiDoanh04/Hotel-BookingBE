package com.example.Hotel_booking.service;




import com.example.Hotel_booking.model.User;
import com.example.Hotel_booking.repository.UserRepository;
import com.example.Hotel_booking.request.AuthRequest;
import com.example.Hotel_booking.request.UpdateUserRequest;
import com.example.Hotel_booking.request.ForgotPasswordRequest;
import com.example.Hotel_booking.request.RequestOtpRequest;
import com.example.Hotel_booking.request.VerifyOtpRequest;
import com.example.Hotel_booking.request.ResetPasswordRequest;
import com.example.Hotel_booking.request.ChangePasswordRequest;
import com.example.Hotel_booking.response.AuthResponse;
import com.example.Hotel_booking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

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
            throw new RuntimeException("Invalid " +
                    "!");
        }
    }

    public String updateUserProfile(UpdateUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này!"));
        
        // Cập nhật thông tin cơ bản
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        
        userRepository.save(user);
        return "Cập nhật thông tin thành công!";
    }

    public String changePassword(String token, ChangePasswordRequest request) {
        // Lấy email từ token
        String email = jwtUtil.extractEmail(token);
        System.out.println(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        
        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }
        
        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới không khớp!");
        }
        
        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        userRepository.save(user);
        return "Đổi mật khẩu thành công!";
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        // Validate if passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }
        
        // Get user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email!"));
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        userRepository.save(user);
        return "Password updated successfully!";
    }

    // Phương thức tạo OTP
    private String generateOtp() {
        // Tạo mã OTP 6 chữ số
        return String.valueOf((int) (Math.random() * 900000 + 100000));
    }

    // Bước 1: Yêu cầu mã OTP
    public String requestOtp(RequestOtpRequest request) {
        System.out.println("vao");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này!"));
        
        // Tạo mã OTP
        String otpCode = generateOtp();
        
        // Lưu mã OTP và thời gian hết hạn (5 phút)
        user.setOtpCode(otpCode);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        
        // Gửi email
        emailService.sendOtpEmail(user.getEmail(), otpCode);
        
        return "Mã OTP đã được gửi đến email của bạn!";
    }

    // Bước 2: Xác thực mã OTP
    public String verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này!"));
        
        // Kiểm tra OTP
        if (user.getOtpCode() == null) {
            throw new RuntimeException("Bạn chưa yêu cầu mã OTP!");
        }
        
        // Kiểm tra thời gian hết hạn
        if (user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }
        
        // Kiểm tra mã OTP
        if (!user.getOtpCode().equals(request.getOtpCode())) {
            throw new RuntimeException("Mã OTP không chính xác!");
        }
        
        return "Xác thực OTP thành công!";
    }

    // Bước 3: Đặt lại mật khẩu
    public String resetPassword(ResetPasswordRequest request) {
        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp!");
        }
        
        User user = userRepository.findByEmailAndOtpCode(request.getEmail(), request.getOtpCode())
                .orElseThrow(() -> new RuntimeException("Email hoặc mã OTP không hợp lệ!"));
        
        // Kiểm tra thời gian hết hạn
        if (user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }
        
        // Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        // Xóa thông tin OTP
        user.setOtpCode(null);
        user.setOtpExpiration(null);
        
        userRepository.save(user);
        return "Mật khẩu đã được đặt lại thành công!";
    }
}

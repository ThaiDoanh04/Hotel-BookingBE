package com.example.Hotel_booking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("doanhthai604204@gmail.com");
            helper.setTo(to);
            helper.setSubject("Mã OTP đặt lại mật khẩu");

            String emailContent = "<html>"
                    + "<body style=\"font-family: Arial, sans-serif;\">"
                    + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                    + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                    + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                    + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                    + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                    + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + otp + "</p>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(emailContent, true);
            mailSender.send(message);
            System.out.println("✅ Email sent successfully!");

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

} 
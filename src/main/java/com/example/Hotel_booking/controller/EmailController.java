package com.example.Hotel_booking.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final JavaMailSender mailSender;
    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @RequestMapping("/send-email")
    public String sendEmail(){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("doanhthai604204@gmail.com");
            message.setTo("ntphong2k4@gmail.com");
            message.setSubject("OTP");
            message.setText("Thai");
            mailSender.send(message);
            return "success";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}

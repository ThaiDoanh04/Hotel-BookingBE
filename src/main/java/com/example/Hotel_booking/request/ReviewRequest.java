package com.example.Hotel_booking.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReviewRequest {
    private String reviewerName;
    private Integer rating;
    private String review;
    private LocalDateTime stayDate;
    private boolean verified;

} 
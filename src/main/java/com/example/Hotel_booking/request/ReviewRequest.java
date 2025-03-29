package com.example.Hotel_booking.request;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private int rating;
    private String review;
    private LocalDateTime stayDate;
    private boolean verified;
} 
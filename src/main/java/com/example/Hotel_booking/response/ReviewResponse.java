package com.example.Hotel_booking.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class ReviewResponse {
    private String reviewerName;
    private Integer rating;
    private String review;
    private LocalDateTime stayDate;
    private boolean verified;
}

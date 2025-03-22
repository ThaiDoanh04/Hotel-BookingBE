package com.example.Hotel_booking.response;

import com.example.Hotel_booking.model.Review;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
@Builder
@Getter
public class HotelReponse {
    private Long hotelId;
    private List<String> images;
    private String title;
    private String subtitle;
    private List<String> benefits;
    private BigDecimal price;
    private Double ratings;
    private String city;
    private List<ReviewResponse> reviews;

}

package com.example.Hotel_booking.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HotelRequest {
    private List<String> images;
    private String title;
    private String subtitle;
    private List<String> benefits;
    private BigDecimal price;
    private String city;
}
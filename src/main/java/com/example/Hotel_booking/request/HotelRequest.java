package com.example.Hotel_booking.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class HotelRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String subtitle;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;
    
    private List<String> images;
    private List<String> benefits;
}
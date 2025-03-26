package com.example.Hotel_booking.request;

import lombok.Data;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelFilterRequest {
    private String city;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private Double maxRating;
    private Integer numOfGuests;
    private String sortDirection = "asc"; // Mặc định sắp xếp tăng dần
    private Integer page = 0;
    private Integer size = 10;
}
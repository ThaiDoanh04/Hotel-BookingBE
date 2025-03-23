package com.example.Hotel_booking.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotelFilterRequest {
    private String city;                 
    private BigDecimal minPrice;       
    private BigDecimal maxPrice;         
    private Double minRating;            
    private Double maxRating;            
    private String keyword;              
    private Integer numOfGuests;
    private Integer page = 0;            
    private Integer size = 10;           
    private String sortBy = "price";    
    private String sortDirection = "asc";
}
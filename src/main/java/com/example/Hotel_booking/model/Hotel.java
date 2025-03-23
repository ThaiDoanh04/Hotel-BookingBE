package com.example.Hotel_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;
    private List<String> images = new ArrayList<>();
    
    private String title;
    private String subtitle;
    private List<String> benefits = new ArrayList<>();
    
    private BigDecimal price;
    private Double ratings;
    private String city;
} 
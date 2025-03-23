package com.example.Hotel_booking.response;

import com.example.Hotel_booking.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelFilterResponse {
    private List<Hotel> hotels;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
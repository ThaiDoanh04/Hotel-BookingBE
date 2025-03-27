package com.example.Hotel_booking.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private int role; // Giá trị số của role (0: USER, 1: ADMIN)

}

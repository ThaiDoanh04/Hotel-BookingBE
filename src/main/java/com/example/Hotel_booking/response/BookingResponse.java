package com.example.Hotel_booking.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.example.Hotel_booking.model.PaymentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String guestFullName;
    private String guestEmail;
    private int numberOfGuests;
    private int numberOfRooms;
    private BigDecimal pricePerNight;
    private BigDecimal totalAmount;
    private String bookingConfirmationCode;
    private Long hotelId;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
}
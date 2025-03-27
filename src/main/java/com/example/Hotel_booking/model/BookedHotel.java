package com.example.Hotel_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Simpson Alfred
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedHotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name = "check_in")
    private LocalDate checkInDate;

    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @Column(name = "guest_fullName")
    private String guestFullName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "number_of_guests")
    private int numberOfGuests;

    @Column(name = "number_of_rooms")
    private int numberOfRooms = 1;

    @Column(name = "price_per_night")
    private BigDecimal pricePerNight;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    private Long userId;

    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}
package com.example.Hotel_booking.repository;

import com.example.Hotel_booking.model.BookedHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookedHotel, Long> {
    Optional<BookedHotel> findByBookingConfirmationCode(String confirmationCode);
    Optional<BookedHotel> findByGuestEmail(String guestEmail);
    Optional<BookedHotel> findByHotelId(Long hotelId);
    List<BookedHotel> findAllByGuestEmail(String guestEmail);
}
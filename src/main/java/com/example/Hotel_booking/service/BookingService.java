package com.example.Hotel_booking.service;

import com.example.Hotel_booking.model.BookedHotel;
import com.example.Hotel_booking.repository.BookingRepository;
import com.example.Hotel_booking.request.BookingRequest;
import com.example.Hotel_booking.response.BookingResponse;
import com.example.Hotel_booking.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public BookingResponse createBooking(BookingRequest request,String token) {
        String email = jwtUtil.extractEmail(token);
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new RuntimeException("Check-in date và check-out date không được để trống.");
        }
        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new RuntimeException("Check-in date phải trước check-out date.");
        }

        BookedHotel bookedHotel = new BookedHotel();
        bookedHotel.setCheckInDate(request.getCheckInDate());
        bookedHotel.setCheckOutDate(request.getCheckOutDate());
        bookedHotel.setGuestFullName(request.getGuestFullName());
        bookedHotel.setGuestEmail(email);
        bookedHotel.setNumOfAdults(request.getNumOfAdults());
        bookedHotel.setNumOfChildren(request.getNumOfChildren());
        bookedHotel.setHotelId(request.getHotelId());
        bookedHotel.setBookingConfirmationCode(UUID.randomUUID().toString());
        bookedHotel.calculateTotalNumberOfGuest();

        return mapToResponse(bookingRepository.save(bookedHotel));
    }

    public BookingResponse getBookingByConfirmationCode(String confirmationCode) {
        BookedHotel bookedHotel = bookingRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("Booking not found with confirmation code: " + confirmationCode));
        return mapToResponse(bookedHotel);
    }

    public List<BookingResponse> getBookingsByCustomerId(String customerId) {
        List<BookedHotel> bookings = bookingRepository.findAllByGuestEmail(customerId);
        if (bookings.isEmpty()) {
            throw new RuntimeException("No bookings found for customer: " + customerId);
        }
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        BookedHotel bookedHotel = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        bookingRepository.delete(bookedHotel);
    }


    private BookingResponse mapToResponse(BookedHotel bookedHotel) {
        return new BookingResponse(
                bookedHotel.getBookingId(),
                bookedHotel.getCheckInDate(),
                bookedHotel.getCheckOutDate(),
                bookedHotel.getGuestFullName(),
                bookedHotel.getGuestEmail(),
                bookedHotel.getNumOfAdults(),
                bookedHotel.getNumOfChildren(),
                bookedHotel.getTotalNumOfGuest(),
                bookedHotel.getBookingConfirmationCode(),
                bookedHotel.getHotelId()
        );
    }
}

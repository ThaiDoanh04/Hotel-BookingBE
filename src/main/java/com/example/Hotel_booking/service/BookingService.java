package com.example.Hotel_booking.service;

import com.example.Hotel_booking.dto.BookingRequest;
import com.example.Hotel_booking.dto.BookingResponse;
import com.example.Hotel_booking.model.BookedHotel;
import com.example.Hotel_booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public BookingResponse createBooking(BookingRequest request) {
        BookedHotel bookedHotel = new BookedHotel();
        bookedHotel.setCheckInDate(request.getCheckInDate());
        bookedHotel.setCheckOutDate(request.getCheckOutDate());
        bookedHotel.setGuestFullName(request.getGuestFullName());
        bookedHotel.setGuestEmail(request.getGuestEmail());
        bookedHotel.setNumOfAdults(request.getNumOfAdults());
        bookedHotel.setNumOfChildren(request.getNumOfChildren());
        bookedHotel.setHotelId(request.getHotelId());

        String confirmationCode = UUID.randomUUID().toString();
        bookedHotel.setBookingConfirmationCode(confirmationCode);
        bookedHotel.calculateTotalNumberOfGuest();

        BookedHotel savedBooking = bookingRepository.save(bookedHotel);
        return mapToResponse(savedBooking);
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
        BookingResponse response = new BookingResponse();
        response.setBookingId(bookedHotel.getBookingId());
        response.setCheckInDate(bookedHotel.getCheckInDate());
        response.setCheckOutDate(bookedHotel.getCheckOutDate());
        response.setGuestFullName(bookedHotel.getGuestFullName());
        response.setGuestEmail(bookedHotel.getGuestEmail());
        response.setNumOfAdults(bookedHotel.getNumOfAdults());
        response.setNumOfChildren(bookedHotel.getNumOfChildren());
        response.setTotalNumOfGuest(bookedHotel.getTotalNumOfGuest());
        response.setBookingConfirmationCode(bookedHotel.getBookingConfirmationCode());
        response.setHotelId(bookedHotel.getHotelId());
        return response;
    }
}
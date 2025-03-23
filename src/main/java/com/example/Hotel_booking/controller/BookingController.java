package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.dto.BookingRequest;
import com.example.Hotel_booking.dto.BookingResponse;
import com.example.Hotel_booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Tạo booking mới
    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest bookingRequest) {
        BookingResponse response = bookingService.createBooking(bookingRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Lấy thông tin booking theo confirmation code
    @GetMapping("/{confirmationCode}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String confirmationCode) {
        BookingResponse response = bookingService.getBookingByConfirmationCode(confirmationCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Lấy tất cả booking của một khách hàng theo customerId (email)
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomer(@PathVariable String customerId) {
        List<BookingResponse> responses = bookingService.getBookingsByCustomerId(customerId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // Hủy booking theo bookingId
    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>("Booking cancelled successfully", HttpStatus.OK);
    }
}
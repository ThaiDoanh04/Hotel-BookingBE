package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.request.BookingRequest;
import com.example.Hotel_booking.response.BookingResponse;
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
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String token) {

        // Loại bỏ tiền tố "Bearer "
        String jwt = token.replace("Bearer ", "");

        BookingResponse response = bookingService.createBooking(request, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lấy thông tin booking theo confirmation code
    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String confirmationCode) {
        BookingResponse response = bookingService.getBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả booking của một khách hàng theo email
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomer(@PathVariable String customerId) {
        List<BookingResponse> responses = bookingService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }
}

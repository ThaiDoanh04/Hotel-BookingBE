package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.request.HotelRequest;
import com.example.Hotel_booking.request.ReviewRequest;
import com.example.Hotel_booking.response.HotelReponse;
import com.example.Hotel_booking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;
    @PostMapping
    public ResponseEntity<Hotel> addHotel(
            @RequestBody HotelRequest hotelRequest) {
        return ResponseEntity.ok(hotelService.addHotel(hotelRequest));
    }
    @PostMapping("/{hotelCode}/reviews")
    public ResponseEntity<Review> addReview(
            @PathVariable String hotelCode,
            @RequestBody ReviewRequest reviewRequest) {

        Long hotelId;
        try {
            hotelId = Long.parseLong(hotelCode);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(hotelService.addReview(hotelId, reviewRequest));
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getHotel() {
        return ResponseEntity.ok(hotelService.getHotel());
    }
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelReponse> getHotelDetail(@PathVariable Long hotelId) {
        System.out.println("vao");
        return ResponseEntity.ok(hotelService.getHotelDetail(hotelId));
    }

    @GetMapping("/{hotelId}/reviews")
    public ResponseEntity<List<Review>> getHotelReviews(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelReviews(hotelId));
    }

} 
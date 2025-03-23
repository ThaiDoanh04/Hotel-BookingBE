package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.request.HotelFilterRequest;
import com.example.Hotel_booking.request.HotelRequest;
import com.example.Hotel_booking.request.ReviewRequest;
import com.example.Hotel_booking.response.HotelFilterResponse;
import com.example.Hotel_booking.response.HotelReponse;
import com.example.Hotel_booking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<?> getHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer numOfGuests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "price") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (city == null && numOfGuests == null && minPrice == null && maxPrice == null &&
            minRating == null && maxRating == null && keyword == null) {
            return ResponseEntity.ok(hotelService.getHotel());
        }
        HotelFilterRequest request = new HotelFilterRequest();
        request.setCity(city);
        request.setNumOfGuests(numOfGuests);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setMinRating(minRating);
        request.setMaxRating(maxRating);
        request.setKeyword(keyword);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        request.setPage(page);
        request.setSize(size);
        
        return ResponseEntity.ok(hotelService.filterHotels(request));
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
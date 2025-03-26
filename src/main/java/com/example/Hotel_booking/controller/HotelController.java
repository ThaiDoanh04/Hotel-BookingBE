package com.example.Hotel_booking.controller;

import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.request.HotelFilterRequest;
import com.example.Hotel_booking.request.HotelRequest;
import com.example.Hotel_booking.request.ReviewRequest;
import com.example.Hotel_booking.response.HotelFilterResponse;
import com.example.Hotel_booking.response.HotelReponse;
import com.example.Hotel_booking.response.MessageResponse;
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

    /**
     * Lấy tất cả khách sạn không có điều kiện
     */
    @GetMapping
    public ResponseEntity<?> getHotels() {
        // Luôn trả về toàn bộ danh sách khách sạn không có filter
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

    @PutMapping("/{hotelId}")
    public ResponseEntity<?> updateHotel(@PathVariable Long hotelId, 
                                       @RequestBody HotelRequest request) {
        try {
            HotelReponse updatedHotel = hotelService.updateHotel(hotelId, request);
            return ResponseEntity.ok(updatedHotel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return ResponseEntity.ok(new MessageResponse("Hotel deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse(e.getMessage()));
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<HotelFilterResponse> filterHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer starRating,
            @RequestParam(required = false) Integer numOfGuests,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        HotelFilterRequest request = new HotelFilterRequest();
        request.setCity(city);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        
        Double minRating = null;
        Double maxRating = null;
        
        if (starRating != null) {
            switch (starRating) {
                case 1:
                    minRating = 0.0;
                    maxRating = 1.0;
                    break;
                case 2:
                    minRating = 1.0;
                    maxRating = 2.0;
                    break;
                case 3:
                    minRating = 2.0;
                    maxRating = 3.0;
                    break;
                case 4:
                    minRating = 3.0;
                    maxRating = 4.0;
                    break;
                case 5:
                    minRating = 4.0;
                    maxRating = 5.0;
                    break;
                default:
                    break;
            }
        }
        
        request.setMinRating(minRating);
        request.setMaxRating(maxRating);
        request.setNumOfGuests(numOfGuests);
        request.setSortDirection(sortDirection);
        request.setPage(page);
        request.setSize(size);
        
        return ResponseEntity.ok(hotelService.filterHotels(request));
    }

    /**
     * Endpoint search hotels theo city và số lượng khách
     */
    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer numOfGuests) {
        
        return ResponseEntity.ok(hotelService.searchHotels(city, numOfGuests));
    }
} 
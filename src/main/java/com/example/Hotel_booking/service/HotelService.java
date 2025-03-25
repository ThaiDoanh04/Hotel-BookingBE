package com.example.Hotel_booking.service;

import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.repository.HotelRepository;
import com.example.Hotel_booking.repository.ReviewRepository;
import com.example.Hotel_booking.request.HotelFilterRequest;
import com.example.Hotel_booking.request.HotelRequest;
import com.example.Hotel_booking.request.ReviewRequest;
import com.example.Hotel_booking.response.HotelFilterResponse;
import com.example.Hotel_booking.response.HotelReponse;
import com.example.Hotel_booking.response.ReviewResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;

    @Transactional
    public Hotel addHotel(HotelRequest request) {
        Hotel hotel = new Hotel();
        hotel.setImages(request.getImages());
        hotel.setTitle(request.getTitle());
        hotel.setSubtitle(request.getSubtitle());
        hotel.setBenefits(request.getBenefits());
        hotel.setPrice(request.getPrice());
        hotel.setCity(request.getCity());
        hotel.setRatings(0.0);
        return hotelRepository.save(hotel);
    }

    private Review convertToReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        review.setReviewerName(reviewRequest.getReviewerName());
        review.setRating(reviewRequest.getRating());
        review.setReview(reviewRequest.getReview());
        review.setStayDate(reviewRequest.getStayDate());
        review.setVerified(reviewRequest.isVerified());
        return review;
    }

    @Transactional
    public Review addReview(Long hotelId, ReviewRequest reviewRequest) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        Review review = convertToReview(reviewRequest);
        review.setHotelId(hotelId);
        reviewRepository.save(review);

        updateHotelRating(hotelId);
        
        return review;
    }

    private void updateHotelRating(Long hotelId) {
        List<Review> reviews = reviewRepository.findByHotelId(hotelId);
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            
            double roundedRating = Math.round(averageRating * 10.0) / 10.0;
            
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            hotel.setRatings(roundedRating);
            hotelRepository.save(hotel);
        }
    }

    public List<Hotel> getHotel() {
        System.out.println("vao");
        return hotelRepository.findAll();
    }
    public HotelReponse getHotelDetail(Long hotelId) {
        if (hotelId == null) {
            throw new IllegalArgumentException("Hotel ID cannot be null");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));

        return toHotelReponse(hotel);
    }

    public List<Review> getHotelReviews(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId);
    }
    public HotelReponse toHotelReponse(Hotel hotel) {
        List<ReviewResponse> reviewResponses = reviewService.getReviewsByHotelId(hotel.getHotelId());
        return HotelReponse.builder()
                .hotelId(hotel.getHotelId())
                .title(hotel.getTitle())
                .subtitle(hotel.getSubtitle())
                .city(hotel.getCity())
                .price(hotel.getPrice())
                .ratings(hotel.getRatings())
                .images(hotel.getImages())
                .benefits(hotel.getBenefits())
                .reviews(reviewResponses)
                .build();
    }
    public HotelFilterResponse filterHotels(HotelFilterRequest request) {
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, request.getSortBy())
        );

        Page<Hotel> hotelPage = hotelRepository.filterHotels(
                request.getCity(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getMinRating(),
                request.getMaxRating(),
                request.getNumOfGuests(),
                request.getKeyword(),
                pageable
        );
        return new HotelFilterResponse(
                hotelPage.getContent(),
                hotelPage.getTotalElements(),
                hotelPage.getTotalPages(),
                hotelPage.getNumber()
        );
    }
} 
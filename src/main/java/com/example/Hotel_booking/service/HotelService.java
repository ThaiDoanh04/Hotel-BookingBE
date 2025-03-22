package com.example.Hotel_booking.service;

import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.repository.HotelRepository;
import com.example.Hotel_booking.repository.ReviewRepository;
import com.example.Hotel_booking.request.HotelRequest;
import com.example.Hotel_booking.request.ReviewRequest;
import com.example.Hotel_booking.response.HotelReponse;
import com.example.Hotel_booking.response.ReviewResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
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
        hotel.setRatings(request.getRatings());
        hotel.setCity(request.getCity());
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
        return review;
    }
    public List<Hotel> getHotel() {
        return hotelRepository.findAll();
    }
    public HotelReponse getHotelDetail(Long hotelId) {
        Hotel hotel= hotelRepository.findById(hotelId).orElseThrow(() -> new RuntimeException("Hotel not found"));
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
                .reviews(reviewResponses) // Nhận dữ liệu từ ReviewService
                .build();
    }
} 
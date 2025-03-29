package com.example.Hotel_booking.service;

import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.model.User;
import com.example.Hotel_booking.repository.ReviewRepository;
import com.example.Hotel_booking.request.ReviewRequest;
import com.example.Hotel_booking.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private Review convertToReview(ReviewRequest reviewRequest, User user) {
        Review review = new Review();
        review.setReviewerName(user.getFirstName() + " " + user.getLastName());
        review.setUserId(user.getUserId());
        review.setRating(reviewRequest.getRating());
        review.setReview(reviewRequest.getReview());
        review.setStayDate(reviewRequest.getStayDate());
        review.setVerified(reviewRequest.isVerified());
        return review;
    }
    private ReviewResponse toReviewResponse(Review review) {
        return ReviewResponse.builder()
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .review(review.getReview())
                .stayDate(review.getStayDate())
                .verified(review.isVerified())
                .build();
    }
    public List<ReviewResponse> getReviewsByHotelId(Long hotelId) {
        List<Review> reviews = reviewRepository.findByHotelId(hotelId);

        return reviews.stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }
}

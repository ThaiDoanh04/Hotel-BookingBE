package com.example.Hotel_booking.repository;

import com.example.Hotel_booking.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHotelId(Long hotelId);
    void deleteByHotelId(Long hotelId);
    
    // Thêm query mới để tìm review theo userId
    List<Review> findByUserId(Long userId);
}
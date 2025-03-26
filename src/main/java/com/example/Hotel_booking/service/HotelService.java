package com.example.Hotel_booking.service;

import com.example.Hotel_booking.model.BookedHotel;
import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.Review;
import com.example.Hotel_booking.repository.BookingRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final BookingRepository bookingRepository;

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
        // Xác định hướng sắp xếp (mặc định tăng dần theo giá)
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        String sortField = "price"; // Luôn sắp xếp theo giá
        
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, sortField)
        );
        
        // Đảm bảo giá trị filter hợp lệ
        BigDecimal minPrice = request.getMinPrice() != null ? request.getMinPrice() : BigDecimal.ZERO;
        BigDecimal maxPrice = request.getMaxPrice() != null ? request.getMaxPrice() : new BigDecimal("999999999");
        
        Double minRating = request.getMinRating() != null ? request.getMinRating() : 0.0;
        Double maxRating = request.getMaxRating() != null ? request.getMaxRating() : 5.0;
        
        Page<Hotel> hotelPage = hotelRepository.filterHotels(
                request.getCity(),
                minPrice,
                maxPrice,
                minRating,
                maxRating,
                request.getNumOfGuests(),
                pageable
        );
        
        return new HotelFilterResponse(
                hotelPage.getContent(),
                hotelPage.getTotalElements(),
                hotelPage.getTotalPages(),
                hotelPage.getNumber()
        );
    }

    /**
     * Search hotels theo city và số lượng khách
     */
    public List<Hotel> searchHotels(String city, Integer numOfGuests) {
        return hotelRepository.searchByBasicCriteria(city, numOfGuests);
    }

    public HotelReponse updateHotel(Long hotelId, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));

        // Cập nhật thông tin
        hotel.setTitle(request.getTitle());
        hotel.setSubtitle(request.getSubtitle());
        hotel.setCity(request.getCity());
        hotel.setPrice(request.getPrice());
        hotel.setImages(request.getImages());
        hotel.setBenefits(request.getBenefits());
        
        // Lưu vào database
        Hotel updatedHotel = hotelRepository.save(hotel);
        
        // Chuyển đổi và trả về response
        return toHotelReponse(updatedHotel);
    }

    @Transactional
    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
        
        // Kiểm tra xem có booking nào liên quan không
        List<BookedHotel> bookings = bookingRepository.findByHotelId(hotelId);
        if (!bookings.isEmpty()) {
            throw new RuntimeException("Cannot delete hotel with existing bookings");
        }
        
        // Xóa reviews trước (nếu có)
        reviewRepository.deleteByHotelId(hotelId);
        
        // Xóa hotel
        hotelRepository.delete(hotel);
    }
} 
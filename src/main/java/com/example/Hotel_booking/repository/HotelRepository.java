package com.example.Hotel_booking.repository;

import com.example.Hotel_booking.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Query("SELECT h FROM Hotel h WHERE " +
            "(:city IS NULL OR h.city = :city) " +
            "AND (:minPrice IS NULL OR h.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR h.price <= :maxPrice) " +
            "AND (:minRating IS NULL OR h.ratings >= :minRating) " +
            "AND (:maxRating IS NULL OR h.ratings <= :maxRating) " +
            "AND (:numOfGuests IS NULL OR EXISTS (SELECT 1 FROM BookedHotel bh WHERE bh.hotelId = h.hotelId AND bh.totalNumOfGuest >= :numOfGuests)) " +
            "AND (:keyword IS NULL OR " +
            "LOWER(h.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.subtitle) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Hotel> filterHotels(
            @Param("city") String city,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating,
            @Param("numOfGuests") Integer numOfGuests,
            @Param("keyword") String keyword,
            Pageable pageable);
}
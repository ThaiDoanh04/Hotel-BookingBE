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
            "(:city IS NULL OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:minPrice IS NULL OR h.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR h.price <= :maxPrice) AND " +
            "(:minRating IS NULL OR h.ratings >= :minRating) AND " +
            "(:maxRating IS NULL OR h.ratings <= :maxRating) AND " +
            "(:numOfGuests IS NULL OR :numOfGuests <= h.maxGuests)")
    Page<Hotel> filterHotels(
            String city,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            Double maxRating,
            Integer numOfGuests,
            Pageable pageable
    );

    @Query("SELECT h FROM Hotel h WHERE " +
            "(:city IS NULL OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:numOfGuests IS NULL OR :numOfGuests <= h.maxGuests)")
    List<Hotel> searchByBasicCriteria(
            String city,
            Integer numOfGuests
    );
}
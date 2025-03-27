package com.example.Hotel_booking.service;

import com.example.Hotel_booking.model.BookedHotel;
import com.example.Hotel_booking.model.Hotel;
import com.example.Hotel_booking.model.PaymentStatus;
import com.example.Hotel_booking.model.User;
import com.example.Hotel_booking.repository.BookingRepository;
import com.example.Hotel_booking.repository.HotelRepository;
import com.example.Hotel_booking.repository.UserRepository;
import com.example.Hotel_booking.request.BookingRequest;
import com.example.Hotel_booking.response.BookingResponse;
import com.example.Hotel_booking.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    /**
     * Tính tổng tiền bao gồm cả thuế 8%
     */
    public BookingResponse createBooking(BookingRequest request, String token) {
        String email = jwtUtil.extractEmail(token);
        
        // Tìm userId từ email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
        
        // Validate dates
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new RuntimeException("Check-in date và check-out date không được để trống.");
        }
        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            throw new RuntimeException("Check-in date không thể sau check-out date.");
        }
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date không thể là ngày trong quá khứ.");
        }

        // Lấy thông tin khách sạn
        Hotel hotel = hotelRepository.findById(request.getHotelId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

        // Số đêm lưu trú
        long numberOfDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        // Nếu check-in và check-out cùng ngày, tính là 1 đêm
        if (numberOfDays == 0) {
            numberOfDays = 1;
        }
        
        // Giá cơ bản trước thuế: giá phòng * số đêm * số phòng
        BigDecimal baseAmount = hotel.getPrice()
            .multiply(BigDecimal.valueOf(numberOfDays))
            .multiply(BigDecimal.valueOf(request.getNumberOfRooms()));
        System.out.println(numberOfDays);
        // Cộng thêm thuế 8%
        BigDecimal taxRate = new BigDecimal("0.08"); // Thuế 8%
        BigDecimal taxAmount = baseAmount.multiply(taxRate);
        
        // Tổng tiền = giá cơ bản + thuế
        BigDecimal totalAmount = baseAmount.add(taxAmount);

        BookedHotel bookedHotel = new BookedHotel();
        bookedHotel.setCheckInDate(request.getCheckInDate());
        bookedHotel.setCheckOutDate(request.getCheckOutDate());
        bookedHotel.setGuestFullName(request.getGuestFullName());
        bookedHotel.setGuestEmail(email);
        bookedHotel.setNumberOfGuests(request.getNumberOfGuests());
        bookedHotel.setNumberOfRooms(request.getNumberOfRooms() > 0 ? request.getNumberOfRooms() : 1);
        bookedHotel.setPricePerNight(hotel.getPrice());
        bookedHotel.setTotalAmount(totalAmount); // Tổng tiền đã bao gồm thuế
        bookedHotel.setHotelId(request.getHotelId());
        bookedHotel.setBookingConfirmationCode(UUID.randomUUID().toString());
        bookedHotel.setPaymentStatus(PaymentStatus.PENDING);
        bookedHotel.setUserId(user.getUserId());

        return mapToResponse(bookingRepository.save(bookedHotel));
    }

    public BookingResponse getBookingByConfirmationCode(String confirmationCode) {
        BookedHotel bookedHotel = bookingRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("Booking not found with confirmation code: " + confirmationCode));
        return mapToResponse(bookedHotel);
    }

    public List<BookingResponse> getBookingsByCustomerId(String customerId) {
        List<BookedHotel> bookings = bookingRepository.findAllByGuestEmail(customerId);
        if (bookings.isEmpty()) {
            throw new RuntimeException("No bookings found for customer: " + customerId);
        }
        return bookings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        BookedHotel bookedHotel = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        bookingRepository.delete(bookedHotel);
    }

    public List<BookingResponse> getAllBookings() {
        List<BookedHotel> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long bookingId) {
        BookedHotel bookedHotel = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking với ID: " + bookingId));
        return mapToResponse(bookedHotel);
    }

    /**
     * Lấy danh sách booking theo ID của người dùng
     * @param userId ID của người dùng
     * @return Danh sách các booking
     */
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        List<BookedHotel> bookings = bookingRepository.findByUserId(userId);
        
        return bookings.stream()
            .map(booking -> {
                // Giả sử bạn đã có một constructor hoặc một builder pattern trong BookingResponse
                // Nếu chưa có, hãy tạo một phương thức chuyển đổi từ Booking sang BookingResponse
                return mapToResponse(booking);
            })
            .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(BookedHotel bookedHotel) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(bookedHotel.getBookingId());
        response.setCheckInDate(bookedHotel.getCheckInDate());
        response.setCheckOutDate(bookedHotel.getCheckOutDate());
        response.setGuestFullName(bookedHotel.getGuestFullName());
        response.setGuestEmail(bookedHotel.getGuestEmail());
        response.setNumberOfGuests(bookedHotel.getNumberOfGuests());
        response.setNumberOfRooms(bookedHotel.getNumberOfRooms());
        response.setPricePerNight(bookedHotel.getPricePerNight());
        response.setTotalAmount(bookedHotel.getTotalAmount());
        response.setBookingConfirmationCode(bookedHotel.getBookingConfirmationCode());
        response.setHotelId(bookedHotel.getHotelId());
        response.setPaymentStatus(bookedHotel.getPaymentStatus());
        response.setPaymentDate(bookedHotel.getPaymentDate());
        return response;
    }
}

package com.example.Hotel_booking.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull(message = "Ngày check-in không được để trống")
    private LocalDate checkInDate;

    @NotNull(message = "Ngày check-out không được để trống")
    private LocalDate checkOutDate;

    @NotBlank(message = "Tên khách không được để trống")
    private String guestFullName;

    @Min(value = 1, message = "Số lượng khách phải lớn hơn 0")
    private int numberOfGuests;

    @Min(value = 1, message = "Số lượng phòng phải lớn hơn 0")
    private int numberOfRooms = 1;

    @NotNull(message = "ID khách sạn không được để trống")
    private Long hotelId;
}
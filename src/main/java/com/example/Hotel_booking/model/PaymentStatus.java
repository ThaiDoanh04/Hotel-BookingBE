package com.example.Hotel_booking.model;

public enum PaymentStatus {
    PENDING,     // Chờ thanh toán
    PROCESSING,  // Đang xử lý thanh toán
    PAID,        // Đã thanh toán
    FAILED,      // Thanh toán thất bại
    CANCELLED    // Đã hủy
} 
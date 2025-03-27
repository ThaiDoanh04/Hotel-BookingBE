package com.example.Hotel_booking.model;

public enum Role {
    USER(0),  // Người dùng thông thường
    ADMIN(1); // Admin
    
    private final int value;
    
    Role(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    // Phương thức chuyển đổi từ giá trị số sang enum
    public static Role fromValue(int value) {
        if (value == 0) return USER;
        if (value == 1) return ADMIN;
        throw new IllegalArgumentException("Không hợp lệ: " + value);
    }
}
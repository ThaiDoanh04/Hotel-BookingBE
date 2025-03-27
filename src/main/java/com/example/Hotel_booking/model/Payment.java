package com.example.Hotel_booking.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Payment implements Serializable {
    private String status;
    private String message;
    private String URL;
}

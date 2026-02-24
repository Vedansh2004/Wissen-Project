package com.company.seatallocation.dto;

import com.company.seatallocation.entity.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDto {

    private String id;
    private String userId;
    private String userName;
    private String seatId;
    private Integer seatNumber;
    private LocalDate bookingDate;
    private BookingStatus status;
}


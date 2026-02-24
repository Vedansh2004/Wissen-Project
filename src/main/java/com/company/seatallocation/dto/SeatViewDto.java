package com.company.seatallocation.dto;

import com.company.seatallocation.entity.enums.SeatType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatViewDto {

    private String seatId;
    private Integer seatNumber;
    private SeatType type;

    private boolean booked;
    private String bookedBy;

    private boolean holiday;
}


package com.company.seatallocation.dto;

import com.company.seatallocation.entity.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {

    private String id;

    private Integer seatNumber;

    private SeatType type;

    private boolean isAvailable;

    private String bookedBy;

    private String bookingStatus;
}

package com.company.seatallocation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {

    @NotBlank
    private String seatId;

    @NotNull
    @FutureOrPresent
    private LocalDate bookingDate;
}


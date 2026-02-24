package com.company.seatallocation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayDto {

    private String id;

    @NotNull
    @FutureOrPresent
    private LocalDate date;

    @NotBlank
    private String description;
}


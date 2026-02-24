package com.company.seatallocation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveDto {

    private String id;

    private String userId;

    @FutureOrPresent
    private LocalDate date;
}

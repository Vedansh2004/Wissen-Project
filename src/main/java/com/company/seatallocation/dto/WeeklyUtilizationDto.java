package com.company.seatallocation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyUtilizationDto {

    private LocalDate date;

    private int totalBookings;

    private int floatingBookings;

    private double utilizationPercentage;

    private double floatingUsagePercentage;
}

package com.company.seatallocation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DashboardStatsDto {

    private int totalSeats;
    private int seatsBookedToday;
    private double utilizationPercent;
    private double floatingUsagePercent;

    private List<HolidayDto> upcomingHolidays;

    private List<LocalDate> weekDays;
    private List<Double> weekUtilization;
}


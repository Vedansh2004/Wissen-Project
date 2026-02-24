package com.company.seatallocation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private int totalSeats;

    private int seatsBookedToday;

    private double utilizationPercentage;

    private double floatingSeatUsagePercentage;

    private List<WeeklyUtilizationDto> weeklyUtilization;

    private List<HolidayDto> upcomingHolidays;

    private List<BookingDto> myBookings;
}

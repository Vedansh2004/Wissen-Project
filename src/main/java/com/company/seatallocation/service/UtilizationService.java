package com.company.seatallocation.service;

import com.company.seatallocation.dto.DashboardStatsDto;
import com.company.seatallocation.dto.HolidayDto;
import com.company.seatallocation.entity.Booking;
import com.company.seatallocation.entity.Seat;
import com.company.seatallocation.entity.enums.BookingStatus;
import com.company.seatallocation.entity.enums.SeatType;
import com.company.seatallocation.mapper.HolidayMapper;
import com.company.seatallocation.repository.BookingRepository;
import com.company.seatallocation.repository.HolidayRepository;
import com.company.seatallocation.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilizationService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    public DashboardStatsDto getDashboardStats() {
        LocalDate today = LocalDate.now();
        List<Seat> seats = seatRepository.findAll();
        int totalSeats = seats.size();

        List<Booking> todayBookings = bookingRepository.findByBookingDateAndStatus(today, BookingStatus.BOOKED);
        int seatsBookedToday = todayBookings.size();

        double utilizationPercent = totalSeats == 0 ? 0.0 :
                (seatsBookedToday * 100.0 / totalSeats);

        Map<String, Seat> seatById = seats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));

        long floatingBooked = todayBookings.stream()
                .filter(b -> {
                    Seat seat = seatById.get(b.getSeatId());
                    return seat != null && seat.getType() == SeatType.FLOATING;
                })
                .count();

        long totalFloating = seats.stream().filter(s -> s.getType() == SeatType.FLOATING).count();
        double floatingUsagePercent = totalFloating == 0 ? 0.0 :
                (floatingBooked * 100.0 / totalFloating);

        List<HolidayDto> upcomingHolidays = holidayRepository
                .findByDateGreaterThanEqualOrderByDateAsc(today)
                .stream()
                .limit(5)
                .map(holidayMapper::toDto)
                .toList();

        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        List<LocalDate> weekDays = weekStart.datesUntil(weekStart.plusDays(7)).toList();

        List<Double> weekUtilization = weekDays.stream()
                .map(d -> dailyUtilization(d, totalSeats))
                .toList();

        return DashboardStatsDto.builder()
                .totalSeats(totalSeats)
                .seatsBookedToday(seatsBookedToday)
                .utilizationPercent(round(utilizationPercent))
                .floatingUsagePercent(round(floatingUsagePercent))
                .upcomingHolidays(upcomingHolidays)
                .weekDays(weekDays)
                .weekUtilization(weekUtilization.stream().map(this::round).toList())
                .build();
    }

    public double dailyUtilization(LocalDate date, int totalSeats) {
        if (totalSeats == 0) {
            return 0.0;
        }
        int booked = bookingRepository
                .findByBookingDateAndStatus(date, BookingStatus.BOOKED)
                .size();
        return booked * 100.0 / totalSeats;
    }

    public double weeklyUtilization(LocalDate start, LocalDate end) {
        List<Seat> seats = seatRepository.findAll();
        int totalSeats = seats.size();
        if (totalSeats == 0) {
            return 0.0;
        }
        List<Booking> bookings = bookingRepository.findByBookingDateBetweenAndStatus(
                start, end, BookingStatus.BOOKED);
        long days = start.datesUntil(end.plusDays(1)).count();
        if (days == 0) {
            return 0.0;
        }
        double totalPossible = totalSeats * days;
        return bookings.size() * 100.0 / totalPossible;
    }

    public double floatingSeatUsage(LocalDate start, LocalDate end) {
        List<Seat> seats = seatRepository.findAll();
        Map<String, Seat> seatById = seats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));
        long totalFloating = seats.stream().filter(s -> s.getType() == SeatType.FLOATING).count();
        if (totalFloating == 0) {
            return 0.0;
        }
        List<Booking> bookings = bookingRepository.findByBookingDateBetweenAndStatus(
                start, end, BookingStatus.BOOKED);
        long floatingBooked = bookings.stream()
                .filter(b -> {
                    Seat seat = seatById.get(b.getSeatId());
                    return seat != null && seat.getType() == SeatType.FLOATING;
                })
                .count();
        long days = start.datesUntil(end.plusDays(1)).count();
        double totalPossible = totalFloating * days;
        return floatingBooked * 100.0 / totalPossible;
    }

    public double designatedSeatWastage(LocalDate start, LocalDate end) {
        List<Seat> seats = seatRepository.findAll();
        List<Seat> designatedSeats = seats.stream()
                .filter(s -> s.getType() == SeatType.DESIGNATED)
                .toList();
        int totalDesignated = designatedSeats.size();
        if (totalDesignated == 0) {
            return 0.0;
        }
        Map<String, Seat> seatById = seats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));
        List<Booking> bookings = bookingRepository.findByBookingDateBetweenAndStatus(
                start, end, BookingStatus.BOOKED);
        long designatedBooked = bookings.stream()
                .filter(b -> {
                    Seat seat = seatById.get(b.getSeatId());
                    return seat != null && seat.getType() == SeatType.DESIGNATED;
                })
                .count();
        long days = start.datesUntil(end.plusDays(1)).count();
        double totalPossible = totalDesignated * days;
        double usedPercent = designatedBooked * 100.0 / totalPossible;
        return 100.0 - usedPercent;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}


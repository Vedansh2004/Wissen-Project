package com.company.seatallocation.service;

import com.company.seatallocation.dto.BookingDto;
import com.company.seatallocation.dto.BookingRequestDto;
import com.company.seatallocation.dto.SeatViewDto;
import com.company.seatallocation.entity.Booking;
import com.company.seatallocation.entity.Holiday;
import com.company.seatallocation.entity.Leave;
import com.company.seatallocation.entity.Seat;
import com.company.seatallocation.entity.User;
import com.company.seatallocation.entity.enums.Batch;
import com.company.seatallocation.entity.enums.BookingStatus;
import com.company.seatallocation.entity.enums.SeatType;
import com.company.seatallocation.exception.BusinessException;
import com.company.seatallocation.exception.NotFoundException;
import com.company.seatallocation.repository.BookingRepository;
import com.company.seatallocation.repository.HolidayRepository;
import com.company.seatallocation.repository.LeaveRepository;
import com.company.seatallocation.repository.SeatRepository;
import com.company.seatallocation.repository.UserRepository;
import com.company.seatallocation.util.CycleUtil;
import com.company.seatallocation.util.DateUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final HolidayRepository holidayRepository;
    private final LeaveRepository leaveRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public BookingDto bookSeat(@Valid BookingRequestDto request) {
        User user = currentUserService.getCurrentUser();
        LocalDate date = request.getBookingDate();
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new NotFoundException("Seat not found"));

        validateBooking(user, seat, date);

        bookingRepository.findByUserIdAndBookingDate(user.getId(), date)
                .ifPresent(b -> {
                    if (b.getStatus() == BookingStatus.BOOKED) {
                        throw new BusinessException("User already has a booking for this date");
                    }
                });

        bookingRepository.findBySeatIdAndBookingDate(seat.getId(), date)
                .ifPresent(b -> {
                    if (b.getStatus() == BookingStatus.BOOKED) {
                        throw new BusinessException("Seat already booked for this date");
                    }
                });

        Booking booking = Booking.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .bookingDate(date)
                .status(BookingStatus.BOOKED)
                .build();

        Booking saved = bookingRepository.save(booking);
        return toDto(saved, user, seat);
    }

    @Transactional
    public void cancelMyBooking(LocalDate date) {
        User user = currentUserService.getCurrentUser();
        Booking booking = bookingRepository.findByUserIdAndBookingDate(user.getId(), date)
                .orElseThrow(() -> new NotFoundException("No booking found for the specified date"));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public List<BookingDto> getMyBookings(LocalDate start, LocalDate end) {
        User user = currentUserService.getCurrentUser();
        List<Booking> all = bookingRepository.findByBookingDateBetweenAndStatus(start, end, BookingStatus.BOOKED);
        Map<String, Seat> seatById = seatRepository.findAll().stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));
        return all.stream()
                .filter(b -> b.getUserId().equals(user.getId()))
                .sorted(Comparator.comparing(Booking::getBookingDate))
                .map(b -> {
                    Seat seat = seatById.get(b.getSeatId());
                    return toDto(b, user, seat);
                })
                .toList();
    }

    public List<SeatViewDto> getSeatGridForDate(LocalDate date) {
        List<Seat> seats = seatRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Seat::getSeatNumber))
                .toList();
        Map<String, Seat> seatById = seats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));
        List<Booking> bookings = bookingRepository.findByBookingDateAndStatus(date, BookingStatus.BOOKED);
        Map<String, Booking> bookingBySeatId = bookings.stream()
                .collect(Collectors.toMap(Booking::getSeatId, b -> b));

        Map<String, User> userById = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        boolean isHoliday = holidayRepository.findByDate(date).isPresent();

        return seats.stream()
                .map(seat -> {
                    Booking booking = bookingBySeatId.get(seat.getId());
                    String bookedBy = null;
                    boolean booked = false;
                    if (booking != null) {
                        booked = true;
                        User user = userById.get(booking.getUserId());
                        bookedBy = user != null ? user.getName() : "Unknown";
                    }
                    return SeatViewDto.builder()
                            .seatId(seat.getId())
                            .seatNumber(seat.getSeatNumber())
                            .type(seat.getType())
                            .booked(booked)
                            .bookedBy(bookedBy)
                            .holiday(isHoliday)
                            .build();
                })
                .toList();
    }

    private void validateBooking(User user, Seat seat, LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new BusinessException("Cannot book seats in the past");
        }

        Holiday holiday = holidayRepository.findByDate(date).orElse(null);
        if (holiday != null) {
            throw new BusinessException("Cannot book on holiday: " + holiday.getDescription());
        }

        // Check if it's weekend
        if (DateUtil.isWeekend(date)) {
            throw new BusinessException("Cannot book on weekends (Saturday and Sunday)");
        }

        Leave leave = leaveRepository.findByUserIdAndDate(user.getId(), date).orElse(null);
        if (leave != null) {
            throw new BusinessException("Cannot book seat on a leave date");
        }

        if (seat.getType() == SeatType.DESIGNATED) {
            validateDesignatedSeatBooking(user, seat, date);
        } else {
            validateFloatingSeatBooking(user, date);
        }
    }

    private void validateDesignatedSeatBooking(User user, Seat seat, LocalDate date) {
        Batch batch = user.getBatch();
        if (CycleUtil.isDesignatedDayForBatch(date, batch)) {
            return;
        }

        if (isCrossBatchAllowed(user, seat, date)) {
            return;
        }

        throw new BusinessException("Designated seats can only be booked on your batch's designated days");
    }

    private boolean isCrossBatchAllowed(User user, Seat seat, LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (!date.equals(today.plusDays(1))) {
            return false;
        }

        if (now.toLocalTime().isBefore(LocalTime.of(18, 0))) {
            return false;
        }

        boolean seatBookedTomorrow = bookingRepository.findBySeatIdAndBookingDate(seat.getId(), date)
                .filter(b -> b.getStatus() == BookingStatus.BOOKED)
                .isPresent();
        if (seatBookedTomorrow) {
            return false;
        }

        Batch userBatch = user.getBatch();
        boolean isDesignatedForOtherBatch =
                !CycleUtil.isDesignatedDayForBatch(date, userBatch);

        return isDesignatedForOtherBatch;
    }

    private void validateFloatingSeatBooking(User user, LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.equals(today.plusDays(1))
                && CycleUtil.isNonDesignatedDayForBatch(date, user.getBatch())) {
            LocalTime now = LocalTime.now();
            if (now.isBefore(LocalTime.of(15, 0))) {
                throw new BusinessException("Floating seats for tomorrow on non-designated days can only be booked after 3 PM today");
            }
        }
    }

    private BookingDto toDto(Booking booking, User user, Seat seat) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setUserId(user.getId());
        dto.setUserName(user.getName());
        dto.setSeatId(seat.getId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}


package com.company.seatallocation.repository;

import com.company.seatallocation.entity.Booking;
import com.company.seatallocation.entity.enums.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {

    Optional<Booking> findBySeatIdAndBookingDate(String seatId, LocalDate bookingDate);

    Optional<Booking> findByUserIdAndBookingDate(String userId, LocalDate bookingDate);

    List<Booking> findByBookingDateAndStatus(LocalDate date, BookingStatus status);

    List<Booking> findByBookingDateBetweenAndStatus(LocalDate start, LocalDate end, BookingStatus status);

    List<Booking> findByStatusAndBookingDateBefore(BookingStatus status, LocalDate date);
}


package com.company.seatallocation.service;

import com.company.seatallocation.dto.LeaveRequestDto;
import com.company.seatallocation.entity.Booking;
import com.company.seatallocation.entity.Leave;
import com.company.seatallocation.entity.User;
import com.company.seatallocation.entity.enums.BookingStatus;
import com.company.seatallocation.exception.BusinessException;
import com.company.seatallocation.repository.BookingRepository;
import com.company.seatallocation.repository.LeaveRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final BookingRepository bookingRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void applyLeave(@Valid LeaveRequestDto dto) {
        User user = currentUserService.getCurrentUser();
        LocalDate date = dto.getDate();
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new BusinessException("Cannot apply leave in the past");
        }

        leaveRepository.findByUserIdAndDate(user.getId(), date)
                .ifPresent(l -> {
                    throw new BusinessException("Leave already applied for this date");
                });

        Leave leave = Leave.builder()
                .userId(user.getId())
                .date(date)
                .build();
        leaveRepository.save(leave);

        bookingRepository.findByUserIdAndBookingDate(user.getId(), date)
                .ifPresent(b -> {
                    b.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(b);
                });
    }
}


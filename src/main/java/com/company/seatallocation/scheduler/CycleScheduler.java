package com.company.seatallocation.scheduler;

import com.company.seatallocation.entity.Booking;
import com.company.seatallocation.entity.enums.BookingStatus;
import com.company.seatallocation.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CycleScheduler {

    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 0 * * SUN") // Every Sunday at midnight
    @Transactional
    public void resetWeeklyCycle() {
        log.info("Resetting weekly cycle - cleaning up old cancelled bookings");
        
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        
        // Delete cancelled bookings older than 30 days
        List<Booking> oldCancelledBookings = bookingRepository.findByStatusAndBookingDateBefore(
                BookingStatus.CANCELLED, cutoffDate);
        
        if (!oldCancelledBookings.isEmpty()) {
            bookingRepository.deleteAll(oldCancelledBookings);
            log.info("Deleted {} old cancelled bookings", oldCancelledBookings.size());
        }
        
        // Log current cycle information
        LocalDate today = LocalDate.now();
        long daysUntilNextCycle = 14 - (ChronoUnit.DAYS.between(
                LocalDate.of(today.getYear(), 1, 1), today) % 14);
        
        log.info("Current cycle week: {}, Days until next cycle reset: {}", 
                getCurrentCycleWeek(), daysUntilNextCycle);
    }

    @Scheduled(cron = "0 0 1 * * *") // Every day at 1 AM
    @Transactional
    public void dailyCleanup() {
        log.info("Running daily cleanup tasks");
        
        LocalDate today = LocalDate.now();
        
        // Auto-cancel bookings for users who applied leave
        List<Booking> todayBookings = bookingRepository.findByBookingDateAndStatus(
                today, BookingStatus.BOOKED);
        
        int cancelledCount = 0;
        for (Booking booking : todayBookings) {
            // This would need to be enhanced to check leave applications
            // For now, just log the bookings for today
            log.debug("Booking found for today: User {}, Seat {}", 
                    booking.getUserId(), booking.getSeatId());
        }
        
        log.info("Daily cleanup completed. Processed {} bookings for today", 
                todayBookings.size());
    }

    private int getCurrentCycleWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
        long daysSinceStart = ChronoUnit.DAYS.between(startOfYear, now);
        long weeksSinceStart = daysSinceStart / 7;
        return (int) (weeksSinceStart % 2) + 1;
    }
}

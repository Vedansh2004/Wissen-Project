package com.company.seatallocation.util;

import com.company.seatallocation.entity.enums.Batch;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
public class BatchUtil {

    public static int getCurrentCycleWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
        long daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(startOfYear, now);
        long weeksSinceStart = daysSinceStart / 7;
        return (int) (weeksSinceStart % 2) + 1;
    }

    public static boolean isDesignatedDayForBatch(Batch batch, LocalDate date) {
        int currentWeek = getCurrentCycleWeek();
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (batch == Batch.BATCH1) {
            if (currentWeek == 1) {
                return dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY;
            } else {
                return dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY;
            }
        } else {
            if (currentWeek == 1) {
                return dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY;
            } else {
                return dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY;
            }
        }
    }

    public static boolean isNonDesignatedDay(LocalDate date) {
        return !isDesignatedDayForBatch(Batch.BATCH1, date) && !isDesignatedDayForBatch(Batch.BATCH2, date);
    }

    public static boolean isAfter6PMPreviousDay(LocalDate bookingDate) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return bookingDate.isAfter(yesterday) || bookingDate.equals(yesterday);
    }

    public static boolean isAfter3PMToday(LocalDate bookingDate) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        if (bookingDate.equals(tomorrow)) {
            return java.time.LocalTime.now().isAfter(java.time.LocalTime.of(15, 0));
        }
        return true;
    }
}

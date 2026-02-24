package com.company.seatallocation.util;

import com.company.seatallocation.entity.enums.Batch;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class CycleUtil {

    private static final LocalDate REFERENCE_START = LocalDate.of(2026, 1, 3); // Monday of week1 reference

    private CycleUtil() {
    }

    public static int getCycleWeek(LocalDate date) {
        long weeks = ChronoUnit.WEEKS.between(REFERENCE_START, date);
        int offset = (int) (weeks % 2);
        if (offset < 0) {
            offset += 2;
        }
        return offset + 1; // 1 or 2
    }

    public static boolean isDesignatedDayForBatch(LocalDate date, Batch batch) {
        int cycleWeek = getCycleWeek(date);
        DayOfWeek dow = date.getDayOfWeek();

        if (batch == Batch.BATCH1) {
            if (cycleWeek == 1) {
                return dow == DayOfWeek.MONDAY || dow == DayOfWeek.TUESDAY || dow == DayOfWeek.WEDNESDAY;
            } else {
                return dow == DayOfWeek.THURSDAY || dow == DayOfWeek.FRIDAY;
            }
        } else {
            if (cycleWeek == 1) {
                return dow == DayOfWeek.THURSDAY || dow == DayOfWeek.FRIDAY;
            } else {
                return dow == DayOfWeek.MONDAY || dow == DayOfWeek.TUESDAY || dow == DayOfWeek.WEDNESDAY;
            }
        }
    }

    public static boolean isNonDesignatedDayForBatch(LocalDate date, Batch batch) {
        return !isDesignatedDayForBatch(date, batch);
    }
}


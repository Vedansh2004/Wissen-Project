package com.company.seatallocation.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtil {

    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public static boolean isHoliday(LocalDate date) {
        return isWeekend(date);
    }
}

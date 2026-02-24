package com.company.seatallocation.controller;

import com.company.seatallocation.dto.BookingDto;
import com.company.seatallocation.dto.DashboardDto;
import com.company.seatallocation.entity.User;
import com.company.seatallocation.service.BookingService;
import com.company.seatallocation.service.CurrentUserService;
import com.company.seatallocation.service.UtilizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UtilizationService utilizationService;
    private final BookingService bookingService;
    private final CurrentUserService currentUserService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = currentUserService.getCurrentUser();
        
        var stats = utilizationService.getDashboardStats();
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(6);
        var myBookings = bookingService.getMyBookings(today, weekEnd);

        DashboardDto dashboardDto = DashboardDto.builder()
                .totalSeats(stats.getTotalSeats())
                .seatsBookedToday(stats.getSeatsBookedToday())
                .utilizationPercentage(stats.getUtilizationPercent())
                .floatingSeatUsagePercentage(stats.getFloatingUsagePercent())
                .upcomingHolidays(stats.getUpcomingHolidays())
                .myBookings(myBookings)
                .build();

        model.addAttribute("dashboard", dashboardDto);
        model.addAttribute("currentUser", user);
        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}

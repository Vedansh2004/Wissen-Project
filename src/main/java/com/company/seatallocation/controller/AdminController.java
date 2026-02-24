package com.company.seatallocation.controller;

import com.company.seatallocation.dto.HolidayDto;
import com.company.seatallocation.dto.UserDto;
import com.company.seatallocation.service.HolidayService;
import com.company.seatallocation.service.UserService;
import com.company.seatallocation.service.UtilizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final HolidayService holidayService;
    private final UtilizationService utilizationService;

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        var stats = utilizationService.getDashboardStats();
        List<UserDto> users = userService.getAllUsers();
        List<HolidayDto> holidays = holidayService.getAllUpcoming();

        model.addAttribute("stats", stats);
        model.addAttribute("users", users);
        model.addAttribute("holidays", holidays);
        return "admin-panel";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute UserDto userDto, Model model) {
        try {
            userService.createUser(userDto);
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userDto);
            return "admin-users";
        }
    }

    @PostMapping("/holidays")
    public String addHoliday(@Valid @ModelAttribute HolidayDto holidayDto, Model model) {
        try {
            holidayService.addHoliday(holidayDto);
            return "redirect:/admin/panel";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin-panel";
        }
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        var stats = utilizationService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin-analytics";
    }
}

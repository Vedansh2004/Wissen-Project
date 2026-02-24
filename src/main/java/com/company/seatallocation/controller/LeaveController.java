package com.company.seatallocation.controller;

import com.company.seatallocation.dto.LeaveRequestDto;
import com.company.seatallocation.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/leave")
    public String leavePage(Model model) {
        model.addAttribute("leaveRequest", new LeaveRequestDto());
        return "leave";
    }

    @PostMapping("/leave")
    public String applyLeave(@Valid @ModelAttribute LeaveRequestDto leaveRequest, Model model) {
        try {
            leaveService.applyLeave(leaveRequest);
            model.addAttribute("success", "Leave applied successfully! Your booking for this date has been cancelled.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "leave";
    }
}

package com.company.seatallocation.controller;

import com.company.seatallocation.dto.BookingRequestDto;
import com.company.seatallocation.dto.BookingDto;
import com.company.seatallocation.dto.SeatViewDto;
import com.company.seatallocation.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/book-seat")
    public String bookSeatPage(Model model) {
        model.addAttribute("bookingRequest", new BookingRequestDto());
        model.addAttribute("today", LocalDate.now());
        return "book-seat";
    }

    @GetMapping("/api/seats")
    @ResponseBody
    public List<SeatViewDto> getSeatGrid(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return bookingService.getSeatGridForDate(date);
    }

    @PostMapping("/book-seat")
    public String bookSeat(@Valid @ModelAttribute BookingRequestDto bookingRequest, Model model) {
        try {
            BookingDto booking = bookingService.bookSeat(bookingRequest);
            model.addAttribute("success", "Seat booked successfully!");
            model.addAttribute("booking", booking);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "book-seat";
    }

    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        try {
            bookingService.cancelMyBooking(date);
            model.addAttribute("success", "Booking cancelled successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate monthEnd = today.plusMonths(1);
        List<BookingDto> bookings = bookingService.getMyBookings(today, monthEnd);
        model.addAttribute("bookings", bookings);
        return "my-bookings";
    }
}

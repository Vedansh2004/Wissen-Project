package com.company.seatallocation.config;

import com.company.seatallocation.entity.*;
import com.company.seatallocation.entity.enums.*;
import com.company.seatallocation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            SeatRepository seatRepository,
            HolidayRepository holidayRepository,
            BookingRepository bookingRepository,
            LeaveRepository leaveRepository) {
        
        return args -> {
            log.info("Initializing seed data...");
            
            // Initialize seats only if no seats exist
            if (seatRepository.count() == 0) {
                initializeSeats(seatRepository);
            }
            
            // Initialize users only if no users exist
            if (userRepository.count() == 0) {
                initializeUsers(userRepository);
            }
            
            // Initialize sample holidays
            if (holidayRepository.count() == 0) {
                initializeHolidays(holidayRepository);
            }
            
            log.info("Seed data initialization completed");
        };
    }

    private void initializeSeats(SeatRepository seatRepository) {
        log.info("Creating 50 seats (10 floating, 40 designated)");
        
        // Create 40 designated seats (1-40)
        for (int i = 1; i <= 40; i++) {
            Seat seat = Seat.builder()
                    .seatNumber(i)
                    .type(SeatType.DESIGNATED)
                    .build();
            seatRepository.save(seat);
        }
        
        // Create 10 floating seats (41-50)
        for (int i = 41; i <= 50; i++) {
            Seat seat = Seat.builder()
                    .seatNumber(i)
                    .type(SeatType.FLOATING)
                    .build();
            seatRepository.save(seat);
        }
        
        log.info("Created {} seats", seatRepository.count());
    }

    private void initializeUsers(UserRepository userRepository) {
        log.info("Creating admin and employee users");
        
        // Create admin users
        User admin1 = User.builder()
                .name("Admin User")
                .email("admin@office.com")
                .password(passwordEncoder.encode("admin123"))
                .batch(Batch.BATCH1)
                .role(Role.ADMIN)
                .createdAt(java.time.Instant.now())
                .build();
        
        User admin2 = User.builder()
                .name("System Admin")
                .email("sysadmin@office.com")
                .password(passwordEncoder.encode("admin123"))
                .batch(Batch.BATCH2)
                .role(Role.ADMIN)
                .createdAt(java.time.Instant.now())
                .build();
        
        userRepository.saveAll(Arrays.asList(admin1, admin2));
        
        // Create employee users (10 per batch)
        for (int i = 1; i <= 10; i++) {
            User employee1 = User.builder()
                    .name("Employee " + i + " (Batch 1)")
                    .email("emp" + i + "@office.com")
                    .password(passwordEncoder.encode("emp123"))
                    .batch(Batch.BATCH1)
                    .role(Role.EMPLOYEE)
                    .createdAt(java.time.Instant.now())
                    .build();
            
            User employee2 = User.builder()
                    .name("Employee " + (i + 10) + " (Batch 2)")
                    .email("emp" + (i + 10) + "@office.com")
                    .password(passwordEncoder.encode("emp123"))
                    .batch(Batch.BATCH2)
                    .role(Role.EMPLOYEE)
                    .createdAt(java.time.Instant.now())
                    .build();
            
            userRepository.saveAll(Arrays.asList(employee1, employee2));
        }
        
        log.info("Created {} users", userRepository.count());
    }

    private void initializeHolidays(HolidayRepository holidayRepository) {
        log.info("Creating sample holidays");
        
        LocalDate currentYear = LocalDate.now();
        
        List<Holiday> holidays = Arrays.asList(
                Holiday.builder()
                        .date(LocalDate.of(currentYear.getYear(), 1, 1)) // New Year
                        .description("New Year's Day")
                        .build(),
                
                Holiday.builder()
                        .date(LocalDate.of(currentYear.getYear(), 1, 26)) // Republic Day
                        .description("Republic Day")
                        .build(),
                
                Holiday.builder()
                        .date(LocalDate.of(currentYear.getYear(), 8, 15)) // Independence Day
                        .description("Independence Day")
                        .build(),
                
                Holiday.builder()
                        .date(LocalDate.of(currentYear.getYear(), 10, 2)) // Gandhi Jayanti
                        .description("Gandhi Jayanti")
                        .build(),
                
                Holiday.builder()
                        .date(LocalDate.of(currentYear.getYear(), 12, 25)) // Christmas
                        .description("Christmas Day")
                        .build(),
                
                // Add weekly holidays (Saturday and Sunday)
                Holiday.builder()
                        .date(LocalDate.of(currentYear.getYear(), currentYear.getMonthValue(), currentYear.getDayOfMonth()))
                        .description("Weekend Holiday")
                        .build()
        );
        
        // Filter out past holidays
        LocalDate today = LocalDate.now();
        List<Holiday> upcomingHolidays = holidays.stream()
                .filter(holiday -> !holiday.getDate().isBefore(today))
                .toList();
        
        holidayRepository.saveAll(upcomingHolidays);
        log.info("Created {} upcoming holidays", upcomingHolidays.size());
    }
}

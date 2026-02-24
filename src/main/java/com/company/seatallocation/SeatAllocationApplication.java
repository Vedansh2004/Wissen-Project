package com.company.seatallocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeatAllocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatAllocationApplication.class, args);
    }
}


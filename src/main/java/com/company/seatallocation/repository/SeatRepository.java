package com.company.seatallocation.repository;

import com.company.seatallocation.entity.Seat;
import com.company.seatallocation.entity.enums.SeatType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeatRepository extends MongoRepository<Seat, String> {

    List<Seat> findByType(SeatType type);
}


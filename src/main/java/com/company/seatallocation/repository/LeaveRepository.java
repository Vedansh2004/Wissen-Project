package com.company.seatallocation.repository;

import com.company.seatallocation.entity.Leave;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRepository extends MongoRepository<Leave, String> {

    Optional<Leave> findByUserIdAndDate(String userId, LocalDate date);

    List<Leave> findByDateBetween(LocalDate start, LocalDate end);
}


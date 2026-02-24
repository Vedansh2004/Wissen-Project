package com.company.seatallocation.repository;

import com.company.seatallocation.entity.Holiday;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends MongoRepository<Holiday, String> {

    Optional<Holiday> findByDate(LocalDate date);

    List<Holiday> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
}


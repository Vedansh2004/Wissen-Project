package com.company.seatallocation.service;

import com.company.seatallocation.dto.HolidayDto;
import com.company.seatallocation.entity.Holiday;
import com.company.seatallocation.exception.BusinessException;
import com.company.seatallocation.mapper.HolidayMapper;
import com.company.seatallocation.repository.HolidayRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    @Transactional
    public HolidayDto addHoliday(@Valid HolidayDto dto) {
        LocalDate date = dto.getDate();
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new BusinessException("Cannot add holiday in the past");
        }
        holidayRepository.findByDate(date)
                .ifPresent(h -> {
                    throw new BusinessException("Holiday already exists for this date");
                });

        Holiday holiday = holidayMapper.toEntity(dto);
        Holiday saved = holidayRepository.save(holiday);
        return holidayMapper.toDto(saved);
    }

    public List<HolidayDto> getAllUpcoming() {
        return holidayRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now())
                .stream()
                .map(holidayMapper::toDto)
                .toList();
    }
}


package com.company.seatallocation.mapper;

import com.company.seatallocation.dto.HolidayDto;
import com.company.seatallocation.entity.Holiday;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HolidayMapper {

    HolidayDto toDto(Holiday entity);

    Holiday toEntity(HolidayDto dto);
}


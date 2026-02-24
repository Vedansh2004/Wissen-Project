package com.company.seatallocation.mapper;

import com.company.seatallocation.dto.SeatDto;
import com.company.seatallocation.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "bookedBy", ignore = true)
    @Mapping(target = "bookingStatus", ignore = true)
    SeatDto toDto(Seat seat);

    @Mapping(target = "id", ignore = true)
    Seat toEntity(SeatDto seatDto);
}

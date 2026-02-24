package com.company.seatallocation.mapper;

import com.company.seatallocation.dto.BookingDto;
import com.company.seatallocation.entity.Booking;
import com.company.seatallocation.entity.User;
import com.company.seatallocation.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Booking toEntity(BookingDto bookingDto);
}

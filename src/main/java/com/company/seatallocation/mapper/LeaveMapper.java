package com.company.seatallocation.mapper;

import com.company.seatallocation.dto.LeaveDto;
import com.company.seatallocation.entity.Leave;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveMapper {

    LeaveDto toDto(Leave leave);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Leave toEntity(LeaveDto leaveDto);
}

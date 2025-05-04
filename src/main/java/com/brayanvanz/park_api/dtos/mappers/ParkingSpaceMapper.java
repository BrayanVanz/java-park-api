package com.brayanvanz.park_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import com.brayanvanz.park_api.dtos.ParkingSpaceCreateDto;
import com.brayanvanz.park_api.dtos.ParkingSpaceResponseDto;
import com.brayanvanz.park_api.entities.ParkingSpace;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpaceMapper {

    public static ParkingSpace toParkingSpace(ParkingSpaceCreateDto dto) {
        return new ModelMapper().map(dto, ParkingSpace.class);
    }

    public static ParkingSpaceResponseDto tDto(ParkingSpace parkingSpace) {
        return new ModelMapper().map(parkingSpace, ParkingSpaceResponseDto.class);
    }
}

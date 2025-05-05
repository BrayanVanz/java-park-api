package com.brayanvanz.park_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import com.brayanvanz.park_api.dtos.ParkingResponseDto;
import com.brayanvanz.park_api.dtos.ParkingSaveDto;
import com.brayanvanz.park_api.entities.ClientParkingSpace;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientParkSpaceMapper {

    public static ClientParkingSpace toClientParkingSpace(ParkingSaveDto dto) {
        return new ModelMapper().map(dto, ClientParkingSpace.class);
    }

    public static ParkingResponseDto tDto(ClientParkingSpace clientParkingSpace) {
        return new ModelMapper().map(clientParkingSpace, ParkingResponseDto.class);
    }
}

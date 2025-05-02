package com.brayanvanz.park_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import com.brayanvanz.park_api.dtos.ClientResponseDto;
import com.brayanvanz.park_api.dtos.ClientSaveDto;
import com.brayanvanz.park_api.entities.Client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {

    public static Client toClient(ClientSaveDto dto) {
        return new ModelMapper().map(dto, Client.class);
    }

    public static ClientResponseDto toDto(Client dto) {
        return new ModelMapper().map(dto, ClientResponseDto.class);
    }
}

package com.brayanvanz.park_api.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.brayanvanz.park_api.dtos.ParkingResponseDto;
import com.brayanvanz.park_api.dtos.ParkingSaveDto;
import com.brayanvanz.park_api.dtos.mappers.ClientParkingSpaceMapper;
import com.brayanvanz.park_api.entities.ClientParkingSpace;
import com.brayanvanz.park_api.services.ParkingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkIn(@RequestBody @Valid ParkingSaveDto dto) {
        ClientParkingSpace clientParkingSpace = ClientParkingSpaceMapper.toClientParkingSpace(dto);
        parkingService.checkIn(clientParkingSpace);
        ParkingResponseDto responseDto = ClientParkingSpaceMapper.tDto(clientParkingSpace);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri().path("/{receipt}")
            .buildAndExpand(clientParkingSpace.getReceipt())
            .toUri();
        
        return ResponseEntity.created(location).body(responseDto);
    }
}

package com.brayanvanz.park_api.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.brayanvanz.park_api.dtos.ParkingSpaceCreateDto;
import com.brayanvanz.park_api.dtos.ParkingSpaceResponseDto;
import com.brayanvanz.park_api.dtos.mappers.ParkingSpaceMapper;
import com.brayanvanz.park_api.entities.ParkingSpace;
import com.brayanvanz.park_api.services.ParkingSpaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/parking-spaces")
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> save(@RequestBody @Valid ParkingSpaceCreateDto dto) {
        ParkingSpace parkingSpace = ParkingSpaceMapper.toParkingSpace(dto);
        parkingSpaceService.save(parkingSpace);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri().path("/{code}")
            .buildAndExpand(parkingSpace.getCode())
            .toUri();
        
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParkingSpaceResponseDto> findByCode(@PathVariable String code) {
        ParkingSpace parkingSpace = parkingSpaceService.findByCode(code);
        return ResponseEntity.ok(ParkingSpaceMapper.tDto(parkingSpace));
    }
}

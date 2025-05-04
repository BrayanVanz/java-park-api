package com.brayanvanz.park_api.controllers;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.brayanvanz.park_api.dtos.ParkingSpaceResponseDto;
import com.brayanvanz.park_api.dtos.ParkingSpaceSaveDto;
import com.brayanvanz.park_api.dtos.mappers.ParkingSpaceMapper;
import com.brayanvanz.park_api.entities.ParkingSpace;
import com.brayanvanz.park_api.exceptions.ErrorMessage;
import com.brayanvanz.park_api.services.ParkingSpaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Parking Spaces", description = "Performs Parking Space related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/parking-spaces")
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @Operation(summary = "Creates a new parking space", description = "Resource used to create a new parking space. " +
        "Requires a bearer token. Access restricted to ADMIN level users", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                headers = @Header(name = HttpHeaders.LOCATION, description = "Created resource URL")),
            @ApiResponse(responseCode = "403", description = "Resource not available to CLIENT level users",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Parking Space already registered",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> save(@RequestBody @Valid ParkingSpaceSaveDto dto) {
        ParkingSpace parkingSpace = ParkingSpaceMapper.toParkingSpace(dto);
        parkingSpaceService.save(parkingSpace);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri().path("/{code}")
            .buildAndExpand(parkingSpace.getCode())
            .toUri();
        
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Finds a parking space", description = "Resource used to find a parking space by its code. " +
        "Requires a bearer token. Access restricted to ADMIN level users", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource localized successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingSpaceResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Resource not available to CLIENT level users",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Parking space not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParkingSpaceResponseDto> findByCode(@PathVariable String code) {
        ParkingSpace parkingSpace = parkingSpaceService.findByCode(code);
        return ResponseEntity.ok(ParkingSpaceMapper.tDto(parkingSpace));
    }
}

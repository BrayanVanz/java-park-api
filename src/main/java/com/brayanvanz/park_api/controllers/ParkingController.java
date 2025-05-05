package com.brayanvanz.park_api.controllers;

import java.net.URI;

import org.springframework.http.HttpHeaders;
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
import com.brayanvanz.park_api.exceptions.ErrorMessage;
import com.brayanvanz.park_api.services.ParkingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Parkings", description = "Performs vehicle check-in and check-out")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/parkings")
public class ParkingController {

    private final ParkingService parkingService;
    
    @Operation(summary = "Check-in operation", description = "Resource used to enter a vehicle into a parking space. " +
        "Requires a bearer token. Access restricted to ADMIN level users", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                headers = @Header(name = HttpHeaders.LOCATION, description = "Created resource URL"),
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Resource not available to CLIENT level users",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Possible causes: <br/>" +
                "- Client CPF not registered within the system; <br/>" +
                "- No available parking space was found; <br/>",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
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

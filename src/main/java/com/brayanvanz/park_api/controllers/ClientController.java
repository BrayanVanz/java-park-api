package com.brayanvanz.park_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brayanvanz.park_api.dtos.ClientResponseDto;
import com.brayanvanz.park_api.dtos.ClientSaveDto;
import com.brayanvanz.park_api.dtos.mappers.ClientMapper;
import com.brayanvanz.park_api.entities.Client;
import com.brayanvanz.park_api.jwt.JwtUserDetails;
import com.brayanvanz.park_api.services.ClientService;
import com.brayanvanz.park_api.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<ClientResponseDto> save(@RequestBody @Valid ClientSaveDto dto,
        @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(dto);
        client.setUser(userService.findById(userDetails.getId()));
        clientService.save(client);

        return ResponseEntity.status(201).body(ClientMapper.toDto(client));
    }
}

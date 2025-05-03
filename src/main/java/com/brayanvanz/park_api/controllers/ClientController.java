package com.brayanvanz.park_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brayanvanz.park_api.dtos.ClientResponseDto;
import com.brayanvanz.park_api.dtos.ClientSaveDto;
import com.brayanvanz.park_api.dtos.PageableDto;
import com.brayanvanz.park_api.dtos.mappers.ClientMapper;
import com.brayanvanz.park_api.dtos.mappers.PageableMapper;
import com.brayanvanz.park_api.entities.Client;
import com.brayanvanz.park_api.exceptions.ErrorMessage;
import com.brayanvanz.park_api.jwt.JwtUserDetails;
import com.brayanvanz.park_api.repositories.projections.ClientProjection;
import com.brayanvanz.park_api.services.ClientService;
import com.brayanvanz.park_api.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Clients", description = "Performs client related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Creates a new client", description = "Resource used to create a new client linked to an existing user. " +
        "Requires a bearer token. Access restricted to CLIENT level users", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Resource not available to ADMIN level users",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Client CPF has already been registered",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<ClientResponseDto> save(@RequestBody @Valid ClientSaveDto dto,
        @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(dto);
        client.setUser(userService.findById(userDetails.getId()));
        clientService.save(client);

        return ResponseEntity.status(201).body(ClientMapper.toDto(client));
    }

    @Operation(summary = "Finds a client", description = "Resource used to find a client by their ID. " +
        "Requires a bearer token. Access restricted to ADMIN level users", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource localized successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Resource not available to CLIENT level users",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Client not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientResponseDto> findById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }

    @Operation(summary = "Retrieves the client list", description = "Resource used to retrieve all clients. " +
        "Requires a bearer token. Access restricted to CLIENT level users", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "Represents a returned page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "size",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                description = "Reperesents the amount of elements in a page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                description = "Represents the sorting type being used. Accepts multiple criteria"
            )
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Resource not available to CLIENT level users",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<ClientProjection>> findAll(@Parameter(hidden = true) Pageable pageable) {
        Page<ClientProjection> clients = clientService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clients));
    }
}

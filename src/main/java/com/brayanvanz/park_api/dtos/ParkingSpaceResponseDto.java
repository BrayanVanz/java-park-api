package com.brayanvanz.park_api.dtos;

import com.brayanvanz.park_api.enums.ParkingSpaceStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ParkingSpaceResponseDto {

    private Long id;
    private String code;
    private ParkingSpaceStatus status;
}

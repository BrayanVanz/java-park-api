package com.brayanvanz.park_api.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brayanvanz.park_api.entities.ParkingSpace;
import com.brayanvanz.park_api.enums.ParkingSpaceStatus;
import com.brayanvanz.park_api.exceptions.CodeUniqueViolationException;
import com.brayanvanz.park_api.exceptions.EntityNotFoundException;
import com.brayanvanz.park_api.repositories.ParkingSpaceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;

    @Transactional
    public ParkingSpace save(ParkingSpace parkingSpace) {
        try {
            return parkingSpaceRepository.save(parkingSpace);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueViolationException(
                String.format("Parking Spot %s already registered", parkingSpace.getCode())
            );
        }
    }

    @Transactional(readOnly = true)
    public ParkingSpace findByCode(String code) {
        return parkingSpaceRepository.findByCode(code).orElseThrow(
            () -> new EntityNotFoundException(String.format("Parking Space %s not found", code))
        );
    }

    @Transactional(readOnly = true)
    public ParkingSpace findFirstByStatus() {
        return parkingSpaceRepository.findFirstByStatus(ParkingSpaceStatus.AVAILABLE).orElseThrow(
            () -> new EntityNotFoundException("No available parking spaces found")
        );
    }
}

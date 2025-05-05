package com.brayanvanz.park_api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brayanvanz.park_api.entities.ParkingSpace;
import com.brayanvanz.park_api.enums.ParkingSpaceStatus;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    Optional<ParkingSpace> findByCode(String code);

    Optional<ParkingSpace> findFirstByStatus(ParkingSpaceStatus available);
}

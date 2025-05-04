package com.brayanvanz.park_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brayanvanz.park_api.entities.ParkingSpace;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

}

package com.brayanvanz.park_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brayanvanz.park_api.entities.ClientParkingSpace;

public interface ClientParkingSpaceRepository extends JpaRepository<ClientParkingSpace, Long> {

}

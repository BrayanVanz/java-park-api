package com.brayanvanz.park_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brayanvanz.park_api.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}

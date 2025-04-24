package com.brayanvanz.park_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brayanvanz.park_api.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

package com.brayanvanz.park_api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.brayanvanz.park_api.entities.User;
import com.brayanvanz.park_api.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    
    @Query("SELECT u.role FROM User u WHERE u.username LIKE :username")
    Role findRoleByUsername(String username);
}

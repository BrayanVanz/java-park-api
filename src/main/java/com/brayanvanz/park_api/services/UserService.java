package com.brayanvanz.park_api.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brayanvanz.park_api.entities.User;
import com.brayanvanz.park_api.enums.Role;
import com.brayanvanz.park_api.exceptions.EntityNotFoundException;
import com.brayanvanz.park_api.exceptions.PasswordInvalidException;
import com.brayanvanz.park_api.exceptions.UsernameUniqueViolationException;
import com.brayanvanz.park_api.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("Username {%s} is already registered", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", id)));
    }

    @Transactional
    public User updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("New password must equal the confirmed password");
        }

        User user = findById(id);
        if (!user.getPassword().equals(currentPassword)) {
            throw new PasswordInvalidException("Wrong password");
        }

        user.setPassword(newPassword);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
            () -> new EntityNotFoundException(String.format("User %s not found", username))
        );
    }

    @Transactional(readOnly = true)
    public Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}

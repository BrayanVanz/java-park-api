package com.brayanvanz.park_api.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brayanvanz.park_api.entities.Client;
import com.brayanvanz.park_api.exceptions.CpfUniqueViolationException;
import com.brayanvanz.park_api.exceptions.EntityNotFoundException;
import com.brayanvanz.park_api.repositories.ClientRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client save(Client client) {
        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new CpfUniqueViolationException(String.format("CPF %s is already registered in the system", client.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Client %s not found", id))
        );
    }
}

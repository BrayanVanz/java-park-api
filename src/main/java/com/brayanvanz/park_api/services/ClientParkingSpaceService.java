package com.brayanvanz.park_api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brayanvanz.park_api.entities.ClientParkingSpace;
import com.brayanvanz.park_api.exceptions.EntityNotFoundException;
import com.brayanvanz.park_api.repositories.ClientParkingSpaceRepository;
import com.brayanvanz.park_api.repositories.projections.ClientParkingSpaceProjection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClientParkingSpaceService {

    private final ClientParkingSpaceRepository clientParkingSpaceRepository;

    @Transactional
    public ClientParkingSpace save(ClientParkingSpace clientParkingSpace) {
        return clientParkingSpaceRepository.save(clientParkingSpace);
    }

    @Transactional(readOnly = true)
    public ClientParkingSpace findByReceipt(String receipt) {
        return clientParkingSpaceRepository.findByReceiptAndExitDateIsNull(receipt).orElseThrow(
            () -> new EntityNotFoundException(String.format("Receipt %s not found or client already checked-out", receipt))
        );
    }

    @Transactional(readOnly = true)
    public long getTimesParkedAndLeft(String cpf) {
        return clientParkingSpaceRepository.countByClientCpfAndExitDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<ClientParkingSpaceProjection> findAllByClientCpf(String cpf, Pageable pageable) {
        return clientParkingSpaceRepository.findAllByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientParkingSpaceProjection> findAllByClientUserId(Long id, Pageable pageable) {
        return clientParkingSpaceRepository.findAllByClientUserId(id, pageable);
    }
}

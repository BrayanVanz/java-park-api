package com.brayanvanz.park_api.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brayanvanz.park_api.entities.Client;
import com.brayanvanz.park_api.entities.ClientParkingSpace;
import com.brayanvanz.park_api.entities.ParkingSpace;
import com.brayanvanz.park_api.enums.ParkingSpaceStatus;
import com.brayanvanz.park_api.utils.ParkingUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ClientParkingSpaceService clientParkingSpaceService;
    private final ClientService clientService;
    private final ParkingSpaceService parkingSpaceService;

    @Transactional
    public ClientParkingSpace checkIn(ClientParkingSpace clientParkingSpace) {
        Client client = clientService.findByCpf(clientParkingSpace.getClient().getCpf());
        clientParkingSpace.setClient(client);

        ParkingSpace parkingSpace = parkingSpaceService.findFirstByStatus();
        parkingSpace.setStatus(ParkingSpaceStatus.TAKEN);
        clientParkingSpace.setParkingSpace(parkingSpace);

        clientParkingSpace.setEntryDate(LocalDateTime.now());
        clientParkingSpace.setReceipt(ParkingUtils.generateReceipt());

        return clientParkingSpaceService.save(clientParkingSpace);
    }

    @Transactional
    public ClientParkingSpace checkOut(String receipt) {
        ClientParkingSpace clientParkingSpace = clientParkingSpaceService.findByReceipt(receipt);

        LocalDateTime exitDate = LocalDateTime.now();
        BigDecimal amount = ParkingUtils.calculateCost(clientParkingSpace.getEntryDate(), exitDate);
        clientParkingSpace.setAmount(amount);

        long times = clientParkingSpaceService.getTimesParkedAndLeft(clientParkingSpace.getClient().getCpf());
        BigDecimal discount = ParkingUtils.calculateDiscount(amount, times);
        clientParkingSpace.setDiscount(discount);

        clientParkingSpace.setExitDate(exitDate);
        clientParkingSpace.getParkingSpace().setStatus(ParkingSpaceStatus.AVAILABLE);

        return clientParkingSpaceService.save(clientParkingSpace);
    }
}

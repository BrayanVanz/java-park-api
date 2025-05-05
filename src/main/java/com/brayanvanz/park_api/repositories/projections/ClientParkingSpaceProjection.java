package com.brayanvanz.park_api.repositories.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public interface ClientParkingSpaceProjection {

    String getPlate();
    String getBrand();
    String getModel();
    String getColor();
    String getClientCpf();
    String getReceipt();

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime getEntryDate();

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime getExitDate();

    String getParkingSpaceCode();
    BigDecimal getAmount();
    BigDecimal getDiscount();
}

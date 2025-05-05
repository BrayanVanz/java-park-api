package com.brayanvanz.park_api.utils;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingUtils {

    public static String generateReceipt() {
        LocalDateTime date = LocalDateTime.now();
        String receipt = date.toString().substring(0, 19);

        return receipt.replace("-", "")
            .replace(":", "")
            .replace("T", "-");
    }
}

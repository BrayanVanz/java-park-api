package com.brayanvanz.park_api.exceptions;

public class CpfUniqueViolationException extends RuntimeException {

    public CpfUniqueViolationException(String message) {
        super(message);
    }
}

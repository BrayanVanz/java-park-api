package com.brayanvanz.park_api.exceptions;

public class CodeUniqueViolationException extends RuntimeException {

    public CodeUniqueViolationException(String message) {
        super(message);
    }
}

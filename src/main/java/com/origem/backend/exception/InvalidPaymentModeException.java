package com.origem.backend.exception;

public class InvalidPaymentModeException extends RuntimeException {
    public InvalidPaymentModeException(String message) {
        super(message);
    }
}

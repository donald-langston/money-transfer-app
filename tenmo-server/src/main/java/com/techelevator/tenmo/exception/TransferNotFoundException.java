package com.techelevator.tenmo.exception;

public class TransferNotFoundException extends RuntimeException {

    public TransferNotFoundException() {
        super();
    }

    public TransferNotFoundException(String message) {
        super(message);
    }
}

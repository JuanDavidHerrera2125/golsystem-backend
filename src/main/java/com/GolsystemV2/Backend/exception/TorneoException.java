package com.GolsystemV2.Backend.exception;

public class TorneoException extends RuntimeException {
    
    public TorneoException(String message) {
        super(message);
    }
    
    public TorneoException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.dexenjaeger.chess.services;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        this(message, null);
    }
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

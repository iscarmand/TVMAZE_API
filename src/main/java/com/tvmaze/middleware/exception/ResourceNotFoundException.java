package com.tvmaze.middleware.exception;

/**
 * @author armand
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}

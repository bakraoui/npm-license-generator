package com.application.exceptions;

public class DependencyNotFoundException  extends RuntimeException{
    
    public DependencyNotFoundException(String message) {
        super(message);
    }
}

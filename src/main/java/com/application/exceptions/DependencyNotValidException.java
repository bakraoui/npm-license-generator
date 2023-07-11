package com.application.exceptions;

public class DependencyNotValidException extends RuntimeException {

    public DependencyNotValidException(String message){
        super(message);
    }
}

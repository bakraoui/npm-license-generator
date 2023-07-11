package com.application.controllers.handlers;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpStatus;
import io.micronaut.serde.annotation.Serdeable.Serializable;

@Introspected
@Serializable
public class ApiError {

    private String message;
    private HttpStatus status;
    
    public ApiError (String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public HttpStatus getStatus() {
        return status;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setStatus(HttpStatus status) {
        this.status = status;
    }
    
}

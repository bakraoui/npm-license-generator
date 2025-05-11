package com.application.controllers;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpStatus;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable.Serializable
@Introspected
public class Response {

    private String filename;
    private HttpStatus status;

    public String getFilename() {
        return filename;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}

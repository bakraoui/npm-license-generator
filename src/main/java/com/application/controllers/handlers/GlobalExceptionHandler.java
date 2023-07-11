package com.application.controllers.handlers;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


@Produces
@Singleton
@Requires(classes = {RuntimeException.class, ExceptionHandler.class})
public class GlobalExceptionHandler 
    implements ExceptionHandler<RuntimeException, HttpResponse<ApiError>> {

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, RuntimeException exception) {
        ApiError error = new ApiError(
            exception.getMessage(), 
            HttpStatus.BAD_REQUEST
        );

        return HttpResponse.serverError()
                .body(error)
                .status(HttpStatus.BAD_REQUEST);
    }


}


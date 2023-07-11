package com.application.controllers.handlers;

import com.application.exceptions.DependencyNotFoundException;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


@Produces
@Singleton
@Requires(classes = {DependencyNotFoundException.class, ExceptionHandler.class})
public class DependencyNotFoundHandler 
implements ExceptionHandler<DependencyNotFoundException, HttpResponse<ApiError>> {

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, DependencyNotFoundException exception) {
        ApiError error = new ApiError(
            exception.getMessage(), 
            HttpStatus.NOT_FOUND
        );

        return HttpResponse.ok()
                .body(error);
    }
    
}

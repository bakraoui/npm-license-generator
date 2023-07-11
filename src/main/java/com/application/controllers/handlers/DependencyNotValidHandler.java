package com.application.controllers.handlers;

import com.application.exceptions.DependencyNotValidException;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


@Produces
@Singleton
@Requires(classes = {DependencyNotValidException.class, ExceptionHandler.class})
public class DependencyNotValidHandler 
implements ExceptionHandler<DependencyNotValidException, HttpResponse<ApiError>>{

    @Override
    public HttpResponse<ApiError> handle(HttpRequest request, DependencyNotValidException exception) {
        ApiError error = new ApiError(
            exception.getMessage(), 
            HttpStatus.BAD_REQUEST
        );

        return HttpResponse.ok()
                .body(error);
    }
    
}

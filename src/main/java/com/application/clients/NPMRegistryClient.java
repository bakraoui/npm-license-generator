package com.application.clients;



import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Retryable;
import reactor.core.publisher.Mono;

@Retryable(attempts = "10")
@Client("${micronaut.application.npmregistry}")
public interface NPMRegistryClient {

    @Get("/{name}/{version}")
    public Mono<String> getDependency(String name, String version);
    
}

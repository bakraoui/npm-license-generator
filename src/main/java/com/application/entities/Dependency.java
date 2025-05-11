package com.application.entities;

import java.util.List;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable.Deserializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Introspected
@Deserializable
public class Dependency {

    private String name;
    private String version;
    private List<Dependency> dependencies;
    private String licenseType;
    private int level = 0;

    public Dependency(){}

    public Dependency(String name, String version){
        this.name = name;
        this.version = version;
    }

    public Dependency(String name, String version, List<Dependency> dependencies){
        this.name = name;
        this.version = version;
        this.dependencies = dependencies;
    }

}

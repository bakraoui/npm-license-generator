package com.application.entities;

import java.util.List;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable.Deserializable;

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

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public int getLevel() {
        return level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void setLevel(int level) {
        this.level = level;
    }

   public void setLicenseType(String licenseType) {
       this.licenseType = licenseType;
   }

}

package com.application.services;

import java.io.File;
import java.io.IOException;

import com.application.entities.Dependency;


public interface LicenceGenerator {
    
    public File getLicenseFile(String name, String version) throws NoSuchFieldException, SecurityException, IOException;
    public void getLicense(Dependency dependency, StringBuilder license) throws NoSuchFieldException, SecurityException, IOException;

}

package com.application.services;

import java.io.File;
import java.io.IOException;

public interface TreeGenerator {

    public File getTreeFile(String name, String version) throws NoSuchFieldException, SecurityException, IOException ;
}
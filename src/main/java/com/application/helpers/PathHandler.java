package com.application.helpers;

import com.application.entities.Dependency;

/**
 *
 * @author Bakraoui
 * 
 */
public class PathHandler {

    private PathHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static String prepareFilePathname(String directory, String depName, String depVersion) {
        String name = depName.replace("/", "_");
        String version = prepareValidVersionName(depVersion).replace("||", "--");
        return "resources/" +
                directory +
                "/" +
                name +
                "@" +
                version +
                ".txt";
    }

    public static String prepareValidVersionName(String version) {
        version = version
                .replace("^", "")
                .replace("~", "")
                .replace("=", "")
                .replace("@", "")
                .replace(" ", "");

        return version.equals("*") ? "latest" : version;
                        
    }
    
}

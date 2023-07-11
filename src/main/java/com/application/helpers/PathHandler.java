/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.application.helpers;

import com.application.entities.Dependency;

/**
 *
 * @author Bakraoui
 * 
 */
public class PathHandler {

    public static String prepareFilePathname(String directory, Dependency dependency){
        String name = dependency.getName().replace("/", "_");
        String version = prepareValidVersionName(dependency.getVersion()).replace("||", "--");
        StringBuilder pathname = new StringBuilder();
        pathname.append("resources/")
                .append(directory)
                .append("/")
                .append(name)
                .append("@")
                .append(version)
                .append(".txt");
        return pathname.toString();
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

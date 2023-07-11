/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.application.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.application.entities.Dependency;

/**
 *
 * @author Bakraoui
 */
public class FileHandler {


    public static void createFile(String directory, String content) throws IOException {
        File file = new File(directory);
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }


    public static String readFileContent(File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder content = new StringBuilder("");
        String line = bufferedReader.readLine();

        if (line.trim().equals("") || line.trim().isBlank() ) {
            line = bufferedReader.readLine();
        }

        while (line != null) {
            content.append(line + "\n");
            line = bufferedReader.readLine();
        }

        bufferedReader.close();
        reader.close();

        return content.toString();
    }


    public static Dependency getDependencyFromCache(Dependency dependency) throws IOException {
        String dependenciesPath = PathHandler.prepareFilePathname("dependencies", dependency);
        List<Dependency> dependencies = new ArrayList<>();
        File file = new File(dependenciesPath);
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        // dependency.setName(line);
        line = bufferedReader.readLine();
        // dependency.setVersion(line);
        line = bufferedReader.readLine();
        dependency.setLicenseType(line);
        line = bufferedReader.readLine();
        while (line != null) {
            String[] array = line.split(" ");
            dependencies.add(new Dependency(array[0], array[1],new ArrayList<>()));
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        reader.close();
        dependency.setDependencies(dependencies);
        return dependency;
    }


}

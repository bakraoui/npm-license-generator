package com.application.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.application.entities.Dependency;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApiResponseMapper {

    private ApiResponseMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Dependency mapNPMRegitryResponseToDependency(String response) throws IOException, SecurityException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = mapper
                    .readValue(response, new TypeReference<>() {
                    });
       
            
        List<Dependency> dependencies = new ArrayList<>();
        
        if(map.containsKey("dependencies")){
            Map<String, String> mapDependencies = (Map<String, String>) map.get("dependencies");
            mapDependencies.forEach((key, value) -> dependencies.add(new Dependency(key, value, null)));
        }

        String licenseType = "";
        if (map.containsKey("license")) {
            licenseType = map.get("license").toString();

        } else if (map.containsKey("licenses")) {
            List<Map<String, String>> licenses = (List<Map<String, String>>) map.get("licenses");
            licenseType = licenses.get(0).get("type");
        }

        Dependency dependency = new Dependency();
        dependency.setName(map.get("name").toString());
        dependency.setVersion(map.get("version").toString());
        dependency.setLicenseType(licenseType);
        dependency.setDependencies(dependencies);
        
        return dependency;
    }



}

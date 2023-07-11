package com.application.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.application.clients.NPMRegistryClient;
import com.application.entities.Dependency;
import com.application.exceptions.DependencyNotFoundException;
import com.application.exceptions.DependencyNotValidException;
import com.application.helpers.FileHandler;
import com.application.helpers.PathHandler;
import com.application.mappers.ApiResponseMapper;
import com.application.services.DependencyValidator.DependencyValidatorResult;
import static com.application.services.DependencyValidator.DependencyValidatorResult.SUCCESS;
import static com.application.services.DependencyValidator.validateVersion;

import ch.qos.logback.classic.Logger;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class TreeGeneratorImpl implements TreeGenerator {
    
    private static final  Logger logger = (Logger) LoggerFactory.getLogger(TreeGeneratorImpl.class);
    private final NPMRegistryClient npmRegistryClient;

    public TreeGeneratorImpl(NPMRegistryClient npmRegistryClient) {
        
        this.npmRegistryClient = npmRegistryClient;
    }

    @Override
    public File getTreeFile(String name, String version) throws NoSuchFieldException, SecurityException, IOException {
        
        name = name.replace("__", "/");
        Dependency dependency = new Dependency(name, version);
        checkDependencyValidation(dependency);

        if(!isDependencyExist(name, version)) {
            logger.error("No package exist: " + name + " " + version);
            throw new DependencyNotFoundException("No package exist, check the name or version of your package");
        }
        
        String pathname = PathHandler.prepareFilePathname("trees", dependency);
        
        File newFile = new File(pathname);

        logger.info("Start generating tree for " + name +"@" + version);
        if (newFile.exists()) {
            logger.info("--------- The tree was generated successfully.");
            return newFile;
        }

        StringBuilder tree = new StringBuilder();

        // tree.append(name + " " + version + "\n");
        tree.append(name)
            .append(" ")
            .append(version)
            .append("\n");
        
        logger.info("--------- Start Extracting List of dependencies.");
        dependency = getListOfDependencies(dependency, 1);
        logger.info("--------- Dependencies Extracted successfully.");
        logger.info("--------- Start creating the tree from dependencies...");
        converListOfDependenciesToTree(dependency, tree);
        logger.info("--------- Tree created successfully.");

        FileWriter writer =  new FileWriter(newFile);
        writer.append(tree);

        logger.info("--------- The tree is ready to download.");

        writer.close();

        return newFile;
    }

    
    private void checkDependencyValidation(Dependency dependency) {
        DependencyValidatorResult result =  DependencyValidator.validateName()
                        .and(validateVersion())
                        .apply(dependency);
        
        if (!result.equals(SUCCESS)) {
            logger.info("--------- The package " + dependency.getName() + "@" + dependency.getVersion() + "is not valid.");
            throw new DependencyNotValidException(result.getMessage());
        }
    }
   
 
    public Dependency getListOfDependencies(Dependency dependency, int level) throws NoSuchFieldException, SecurityException, IOException {
        
        String validVersion = PathHandler.prepareValidVersionName(dependency.getVersion()); 

        String path = PathHandler.prepareFilePathname("dependencies", dependency);
        File dependencyFile = new File(path);
        
        logger.info(dependency.getName()+" "+dependency.getVersion());
        Dependency nodePackage;
        if (dependencyFile.exists()) {
            nodePackage = FileHandler.getDependencyFromCache(dependency);
        }else {
            nodePackage = getDependencyIfExistByNameAndVersion(dependency.getName(), validVersion);
            StringBuilder dependencyDetails = new StringBuilder();
            dependencyDetails.append(dependency.getName()).append("\n")
                             .append(dependency.getVersion()).append("\n")
                             .append(nodePackage.getLicenseType());
            
            for (Dependency dep : nodePackage.getDependencies()) {
                dependencyDetails.append("\n").append(dep.getName()).append(" ").append(dep.getVersion());
            }
            String pathname = PathHandler.prepareFilePathname("dependencies", dependency);
            FileHandler.createFile(pathname, dependencyDetails.toString());
            
        }
        

        List<Dependency> dependencies = new ArrayList<>();
        
        for (Dependency dep : nodePackage.getDependencies()) {
            logger.info("------- Start extracting Dependencies for " + dep.getName() +"@" +dep.getVersion());
            dep.setLevel(level);
            Dependency p = getListOfDependencies(dep,  level + 1);
            dependencies.add(p);
            logger.info("------- End of Extracting Dependencies for " + dep.getName() +"@" +dep.getVersion());

        }
        dependency.setDependencies(dependencies);
        return dependency;
    }


    private void converListOfDependenciesToTree(Dependency dependency, StringBuilder tree) {
    
        logger.info("------- Start coverting Dependencies to tree " );

        for (Dependency dep : dependency.getDependencies()) {
            if(tree.toString().contains("+-+ " + dep.getName() + " " + dep.getVersion())){
                tree.append("|   ".repeat(dep.getLevel()))
                    .append("|+-+ ")
                    .append(dep.getName())
                    .append(" ")
                    .append(dep.getVersion())
                    .append("\n");
                continue;
            }
            tree.append("|   ".repeat(dep.getLevel()))
                    .append("|+-+ ")
                    .append(dep.getName())
                    .append(" ")
                    .append(dep.getVersion())
                    .append("\n");
            converListOfDependenciesToTree(dep, tree);
            
        }

        logger.info("------- Coverting Dependencies to tree is completed." );


    }
    

    public boolean isDependencyExist(String name, String version) {
        Mono<String> nodeResponse = npmRegistryClient.getDependency(name, version);
        return nodeResponse.block() != null;
    }
    

    public Dependency getDependencyIfExistByNameAndVersion(String name, String version) throws NoSuchFieldException, SecurityException, IOException   {
        Dependency dependency;
        try {
            Mono<String> nodeResponse = npmRegistryClient.getDependency(name, version);
            dependency = ApiResponseMapper.mapNPMRegitryResponseToDependency(nodeResponse.block());
        } catch (Exception e) {
            throw new RuntimeException("We encounter a internal error, please try later.");
        }
        return dependency;
    }


   
}



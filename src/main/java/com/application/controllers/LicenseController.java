package com.application.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

import com.application.exceptions.LicenseNotFoundException;
import com.application.helpers.PathHandler;
import com.application.services.LicenceGenerator;
import com.application.services.TreeGenerator;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.types.files.SystemFile;

@Controller()
public class LicenseController {


    private final LicenceGenerator licenceGenerator;
    private final TreeGenerator treeGenerator;

    public LicenseController( LicenceGenerator licenceGenerator, TreeGenerator treeGenerator) {
        this.licenceGenerator = licenceGenerator;
        this.treeGenerator = treeGenerator;
    }

    @Post(value = "/{name}/{version}")
    public Response getLicense(String name, String version) throws NoSuchFieldException, SecurityException, IOException{
        
        PrintWriter pw = new PrintWriter("LogFile.log");
        pw.close();

        File treefile = treeGenerator.getTreeFile(name, version);
        File licenseFile = licenceGenerator.getLicenseFile(name, version);

        Response response = new Response();
        response.setFilename(treefile.getName());
        response.setStatus(HttpStatus.OK);

        try {
            
            String pathname = PathHandler.prepareFilePathname("logs", name, version);
            File logsNewFileDirectory = new File(pathname);

            if (!logsNewFileDirectory.exists()) {
                FileChannel dest = new FileOutputStream(logsNewFileDirectory).getChannel();

                File logsOldFileDirectory = new File("LogFile.log");
                FileChannel src = new FileInputStream(logsOldFileDirectory).getChannel();
                
                dest.transferFrom(src, 0, src.size()); 
                src.close();
                dest.close();
            }

        } catch (Exception e) {
            throw new RuntimeException("Log File not created.");
        }

        return response;    
                        
    }

    @Get("/license/{filename}")
    public SystemFile downloadLicense(String filename){
        try {
            File file = new File("resources/licenses/" + filename);
            return new SystemFile(file).attach("license:"+filename);
        } catch (Exception e) {
            throw new LicenseNotFoundException("License Not Found. please generate it again.");
        }
        
    }
    

    @Get("/tree/{filename}")
    public SystemFile downloadTree(String filename){
        try {
            File file = new File("resources/trees/" + filename);
            return new SystemFile(file).attach("tree:"+filename);
        } catch (Exception e) {
            throw new LicenseNotFoundException("License Not Found. please generate it again.");
        }
        
    }

    @Get("/log/{filename}")
    public SystemFile downloadLogs(String filename){
        try {
            File file = new File("resources/logs/" + filename);
            return new SystemFile(file).attach("log:"+filename);
        } catch (Exception e) {
            throw new LicenseNotFoundException("LogFile Not Found. ");
        }
        
    }

}


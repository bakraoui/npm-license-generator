package com.application.services;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import com.application.entities.Dependency;
import com.application.helpers.FileHandler;
import com.application.helpers.PathHandler;

import ch.qos.logback.classic.Logger;
import jakarta.inject.Singleton;

@Singleton
public class LicenceGeneratorImpl implements LicenceGenerator {

    private final Logger logger = (Logger) LoggerFactory.getLogger(LicenceGeneratorImpl.class);
    
    private static final String LICENSE = "licen";
    private static final String NOTICE = "notice";
    private static final String CONTRIBUTE = "contribut";
    private static final String HTTPS_UNPKG_COM_BROWSE = "https://unpkg.com/browse/";
    

    @Override
    public File getLicenseFile(String name, String version) throws SecurityException, IOException {
        
        name = name.replace("__", "/");
        Dependency dependency = new Dependency(name, version);

        String pathname = PathHandler.prepareFilePathname("licenses",name, version);
        File licenseFile = new File(pathname);
        
        if (licenseFile.exists()) {
            return licenseFile;
        }

        
        logger.info("Start generating your license...");
        StringBuilder license = new StringBuilder();
        getLicense(dependency, license); 
        logger.info("\tlicense content is generated successfully");

        try (FileWriter writer = new FileWriter(licenseFile)) {
            logger.info("\tstart creating license file...");
            writer.write(license.toString());
            logger.info("\twriting license content to license file");
            logger.info("License Generated Successfully.");
        }
        return licenseFile;
    }


    @Override
    public void getLicense(Dependency dependency, StringBuilder license) throws SecurityException, IOException {

        String dependencyName = dependency.getName();
        String dependencyVersion = dependency.getVersion();
        if(license.toString().contains("- " + dependencyName)){
            return;
        }

        String licensePathname = PathHandler.prepareFilePathname("sub-licenses", dependencyName, dependencyVersion);
        String noticePathname = PathHandler.prepareFilePathname("notices", dependencyName, dependencyVersion);
        String contributesPathname = PathHandler.prepareFilePathname("contributes", dependencyName, dependencyVersion);
        String copyrightsPathname = PathHandler.prepareFilePathname("copyrights", dependencyName, dependencyVersion);
        String dependencyPathname = PathHandler.prepareFilePathname("dependencies", dependencyName, dependencyVersion);

        File licenseFile = new File(licensePathname);
        File noticeFile = new File(noticePathname);
        File contributesFile = new File(contributesPathname);
        File copyrightsFile = new File(copyrightsPathname);
        File dependencyFile = new File(dependencyPathname);

        String version = PathHandler.prepareValidVersionName(dependency.getVersion());
        dependency.setVersion(version);

        logger.info("\tStart generation license for:  {}@{}", dependencyName, version);

        if (dependencyFile.exists()) {
            dependency = FileHandler.getDependencyFromCache(dependency);
        }
        
        license.append("\n------------------ START OF DEPENDENCY LICENSE --------------------\n")
               .append("- " )
               .append(dependencyName);

        String copyrights;
        
        if (copyrightsFile.exists()) {
            copyrights = FileHandler.readFileContent(copyrightsFile);
        } 
        else {
            copyrights = getCopyrights(dependency);
            FileHandler.createFile(copyrightsPathname, copyrights);
        }

        if (!copyrights.trim().isEmpty()) {
            license.append("\n\n").append(copyrights).append("\n");
        }

        Map<String, String> filenames = getLicenseNoticeContributesFileNames(dependency);

        if (licenseFile.exists()) {
            
            String licenseContent = FileHandler.readFileContent(licenseFile);
            
            if (dependency.getLicenseType() == null || dependency.getLicenseType().isEmpty()) {
                String licenseType = getLicenseType(licenseContent);
                if (licenseType.isEmpty()) {
                    license.append("\n").append(licenseContent).append("\n");
                }
                else {
                    if (!license.toString().contains(licenseType)) {
                        license.append("\n").append("License Type: " ).append(licenseType).append("\n");
                        license.append(licenseContent);
                    }
                    else {
                        license.append("\n").append("License Type: ").append(licenseType).append("\n");
                    }
                }
                
            }
            else if (!license.toString().contains(dependency.getLicenseType())) {
                license.append("\n").append("License Type: " )
                    .append(dependency.getLicenseType()).append("\n");
                license.append(licenseContent);
            }
            else {
                license.append("\n").append("License Type: "  )
                    .append(dependency.getLicenseType()).append("\n");
            }

        } 
        else {
            String licenseName = filenames.get(LICENSE);
            if(!licenseName.isEmpty()){
                String url = prepareURLToBrowseFile(dependency, licenseName);
                String subLicense = getFileContentFromUnpkg( url );
                if ( dependency.getLicenseType() == null || dependency.getLicenseType().isEmpty()) {
                    String licenseType = getLicenseType(subLicense);
                    if (licenseType.isEmpty()) {
                        license.append("\n").append(subLicense).append("\n");
                    }else {
                        if (!license.toString().contains(licenseType)) {
                            license.append("\n").append("License Type: " ).append(licenseType).append("\n");
                            license.append(subLicense);
                        }else {
                            license.append("\n").append("License Type: ").append(licenseType).append("\n");
                        }
                    }
                }
                else if (!license.toString().contains(dependency.getLicenseType())) {
                    license.append("License Type: ")
                        .append(dependency.getLicenseType() != null ? dependency.getLicenseType() : "")
                        .append("\n");
                    license.append(subLicense);
                }
                else {
                    license.append("\n").append("License Type: " )
                    .append(dependency.getLicenseType()).append("\n").append("\n");
                }
                
                FileHandler.createFile(licensePathname, subLicense);
                
            } 
            else {
                logger.info("\t** License not founded.");
            }
            
        }
        

        if (noticeFile.exists()) {
            String noticeContent = FileHandler.readFileContent(noticeFile);
            license.append("\n--------- Notice ---------\n")
                    .append(noticeContent).append("\n");
        }
        else {
            String noticeName = filenames.get(NOTICE);
            if(!noticeName.isEmpty()){
                String notice = getFileContentFromUnpkg( HTTPS_UNPKG_COM_BROWSE + dependencyName + "@" + version + "/" + noticeName );
                license.append("\n--------- Notice ---------\n")
                        .append(notice).append("\n");
           
                FileHandler.createFile(noticePathname, notice);
                
            } else {
                logger.info("\t** Notice not founded.");
            }
            
        }


        if (contributesFile.exists()) {
            String contributesContent = FileHandler.readFileContent(contributesFile);
            license.append("\n--------- Contributes ---------\n")
                    .append(contributesContent).append("\n");
        } 
        else {
            String contributesName = filenames.get(CONTRIBUTE);
            if(!contributesName.isEmpty()){
                String url = prepareURLToBrowseFile(dependency, dependencyName);
                String contributes = getFileContentFromUnpkg( url );
                license.append("\n--------- Contributes ---------\n")
                        .append(contributes)
                        .append("\n");
           
                FileHandler.createFile(contributesPathname, contributes);
                
            } else {
                logger.info("\t** Contributes not founded." );
            }
            
        }
        license.append("------------------ END OF DEPENDENCY LICENSE --------------------\n\n\n\n");

        for (Dependency dep : dependency.getDependencies()) {
            getLicense(dep, license);
        }
        
    }
    

    public String getFileContentFromUnpkg(String url) throws IOException  {
    
        StringBuilder fileContent = new StringBuilder();
        Elements elements;
        try {
            Document document = Jsoup.connect(url).header( "Cookie", "ZvcurrentVolume=100; zvAuth=1; zvLang=0; ZvcurrentVolume=100; notice=11").get();
            elements = document.getElementsByAttributeValueContaining("id", "lc");
        } catch (Exception e) {
            logger.error("\tFailed to get content from: {}", url);
            return "";
        }
        

        elements.forEach(element -> { 
            String line = element.getElementsByTag("code").text();
            if (
                !line.contains("(c)") 
                && !line.contains("Copyright ")
                ) {
                fileContent.append( line).append("\n");
            }
        });

        fileContent.append("\n");

        return fileContent.toString();
              
    }

  
    public Map<String, String> getLicenseNoticeContributesFileNames(Dependency dependency) throws IOException {
               
        String urlToBrowseFile = prepareURLToBrowseFile(dependency, "");
        Document document;

        Map<String, String> filesFullName = new HashMap<>();
        filesFullName.put(LICENSE, "");
        filesFullName.put(NOTICE, "");
        filesFullName.put(CONTRIBUTE, "");

        try {
            document = Jsoup.connect(urlToBrowseFile)
                .header("Cookie", "ZvcurrentVolume=100; zvAuth=1; zvLang=0; ZvcurrentVolume=100; notice=11")
                .get();
        } catch (Exception e) {
            logger.error("\tCannot get License, Contributes, notices for : {}@{} ", dependency.getName(), dependency.getVersion());
            return filesFullName;
        }
        

        Elements license =  document.getElementsByAttributeValueContaining("title", LICENSE);
        Elements notice =  document.getElementsByAttributeValueContaining("title", NOTICE);
        Elements contributes =  document.getElementsByAttributeValueContaining("title", CONTRIBUTE);

        String licenseFullName = !license.isEmpty() ? license.first().text(): "";
        String noticeFullName = !notice.isEmpty() ? notice.first().text(): "";
        String contributesFullName = !contributes.isEmpty() ? contributes.first().text(): "";

        filesFullName.put(LICENSE, licenseFullName);
        filesFullName.put(NOTICE, noticeFullName);
        filesFullName.put(CONTRIBUTE, contributesFullName);
        
        return filesFullName;
    }


    private String prepareURLToBrowseFile(Dependency dependency, String filename){
        StringBuilder url = new StringBuilder(HTTPS_UNPKG_COM_BROWSE);

        url.append(dependency.getName())
            .append("@")
            .append(dependency.getVersion())
            .append("/")
            .append(filename);
        return url.toString();
    }


    private String getCopyrights(Dependency dependency) throws IOException {

        String urlToBrowseFile = prepareURLToBrowseFile(dependency, "");
        
        Elements elements;
        try {
            Document document = Jsoup.connect(urlToBrowseFile).header("Cookie", "ZvcurrentVolume=100; zvAuth=1; zvLang=0; ZvcurrentVolume=100; notice=11").get();
            elements = document.getElementsByAttribute("title");
        } catch (Exception e) {
            logger.error("\tFailed to get copyrights for: " + dependency.getName()+"@"+dependency.getVersion());
            return "";
        }
        
        StringBuilder copyrights = new StringBuilder();
        for (Element element : elements) {
            String l = element.attr("href");
            // getCopyright(dependency, urlToBrowseFile + l, copyrights);

            // Added Content
            if (!l.endsWith("/")) {
                String url = prepareURLToBrowseFile(dependency, l);
                Connection connection = Jsoup.connect(url);
                Document doc ;
                try {
                    doc = connection.header("Cookie", "ZvcurrentVolume=100; zvAuth=1; zvLang=0; ZvcurrentVolume=100; notice=11").get();
                } catch (Exception e) {
                    logger.info("\tCannot access: " + url);
                    continue;
                }
                Elements els = doc.getElementsByAttributeValueContaining("id", "lc");
                
                els.forEach(el -> {
                    String line = el.text();
                    line = line.replace("*", "")
                        .replace("/*", "")
                        .replace("//", "")
                        .replace("#", "")
                        .trim();
                    if (
                        (line.contains("Copyright ")) 
                        && line.length() < 200
                        && !copyrights.toString().contains(line)) {
                        copyrights.append("\n").append(line);
                    }
                    
                });
            }
        }

        return copyrights.toString();
    }


    String getLicenseType(String license) {
        
        if (license.contains("Apache ")) {
            return "Apache";
        }

        if (license.contains("MIT ") || license.contains("THE SOFTWARE IS PROVIDED")) {
            return "MIT";
        }

        if (license.contains("ISC")) {
            return "ISC";
        }

        if (license.contains("BSD")) {
            return "BSD";
        }

        return "";
    
    }
}    

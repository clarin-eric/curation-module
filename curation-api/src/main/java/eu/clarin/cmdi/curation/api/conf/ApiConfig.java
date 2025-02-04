package eu.clarin.cmdi.curation.api.conf;

import eu.clarin.cmdi.vlo.config.DefaultVloConfigFactory;
import eu.clarin.cmdi.vlo.config.VloConfig;
import eu.clarin.cmdi.vlo.config.XmlVloConfigFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

@Component
@ConfigurationProperties(prefix = "curation")
@Data
@Slf4j
public class ApiConfig {
   
   private String mode;
   
   private DataSize maxFileSize;
   
   private Collection<String> facets;
   
   private Directory directory = new Directory();
   
   private String vloConfigLocation;
   
   private String linkDataSource;
   
   private String crQuery;
   
   private String clientUsername;
   
   private String clientPassword;

   private int maxInQueue;
   
   @Bean
   public VloConfig vloConfig() {
      try {
         if (vloConfigLocation == null || vloConfigLocation.isEmpty()) {
            log.warn(
                  "loading default VloConfig.xml from vlo-commons.jar - PROGRAM WILL WORK BUT WILL PROBABLY DELIVER UNATTENDED RESULTS!!!");
            log.warn("make sure to define a valid curation:vlo_config_location in the file application.yml");
            return DefaultVloConfigFactory.configureDefaultMappingLocations(new DefaultVloConfigFactory().newConfig());
         }
         else {
            log.info("loading VloConfig.xml from location {}", vloConfigLocation);
            return new XmlVloConfigFactory(new File(vloConfigLocation).toURI().toURL())
                  .newConfig();
         }
      }
      catch(IOException ex) {
         log.error("couldn't create instance of VloConfig");
         throw new RuntimeException(ex);
      }
      
   }
   

   
   
   @Data
   public static class Directory {
      
      private Path home;
      
      private Path dataRoot;
      
      private Collection<Path> in;
      
      private Path out;
      
   }
}

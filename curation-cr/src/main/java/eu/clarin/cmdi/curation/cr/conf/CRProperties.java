package eu.clarin.cmdi.curation.cr.conf;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "curation")
@Data
public class CRProperties {
   
   private Path xsdCache;
   
   private String crRestUrl;
   
   private String crQuery;
}

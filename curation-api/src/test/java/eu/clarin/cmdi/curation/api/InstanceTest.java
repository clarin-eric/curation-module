/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.cmdi.curation.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import eu.clarin.cmdi.curation.api.report.CMDInstanceReport;
import eu.clarin.cmdi.curation.api.report.Report;

/**
 *
 */
@SpringBootTest
@Import(TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
public class InstanceTest {
   
   private Report<?> report;
   
   @Autowired
   CurationModule curation;
   
   @BeforeAll
   public void createInstanceReport() throws URISyntaxException {
      
      URI instanceURI = this.getClass().getClassLoader().getResource("instance/SAW_Leipzig_Repository/cts_muqtabas_urn_cts_muqtabas_oclc_4770057679_i_29_TEIP5_.xml").toURI();
      Path instancePath = Paths.get(instanceURI);
      
      this.report = curation.processCMDInstance(instancePath);
      
   }
   
   @Test
   @Order(1)
   public void isInstanceReport() {
      
      assertTrue(this.report instanceof CMDInstanceReport);
      
   }
   
   @Test
   @Order(2)
   public void selectionReportsNotNull() {
      
      CMDInstanceReport instanceReport = CMDInstanceReport.class.cast(report);
      
      assertNotNull(instanceReport.header);
      
      assertNotNull(instanceReport.fileReport);
      
      assertNotNull(instanceReport.resProxyReport);
      
      assertNotNull(instanceReport.xmlPopulatedReport);
      
      assertNotNull(instanceReport.xmlValidityReport);
      
      assertNotNull(instanceReport.urlReport);
      
      assertNotNull(instanceReport.facets);
      
      this.report.toXML(System.out);
      
   }

}

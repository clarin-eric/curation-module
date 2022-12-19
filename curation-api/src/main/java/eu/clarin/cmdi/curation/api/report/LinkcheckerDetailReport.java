/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.cmdi.curation.api.report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.clarin.linkchecker.persistence.utils.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@XmlRootElement(name = "linkchecker-detail-report")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinkcheckerDetailReport extends Report<LinkcheckerDetailReport>{
   
   @XmlAttribute()
   private String name; 
   @XmlAttribute(name = "creation-time")
   @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
   private LocalDateTime creationTime = LocalDateTime.now();
   @XmlElement(name = "category")
   private final Collection<CategoryReport> categoryReports = new TreeSet<CategoryReport>((report1, report2) -> report1.category.compareTo(report2.category));

   public LinkcheckerDetailReport() {
      super();
   }
   
   public LinkcheckerDetailReport(String name) {
      
      super();
      this.name = name;
      
   }

   @Override
   public String getName() {
      
      return this.name;
      
   }

   @Override
   public boolean isValid() {

      return true;
   
   }

   @Override
   public void addSegmentScore(Score segmentScore) {
      
      // do nothing
      
   }

   @Override
   public void addReport(LinkcheckerDetailReport parentReport) {
      
      // do nothing
      
   }
   
   public Collection<CategoryReport> getCategoryReports(){
      
      return this.categoryReports;
      
   }
   
   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlRootElement
   @Data
   public static class CategoryReport {
      
      
      @XmlAttribute(name = "name")
      private Category category;
      @XmlElement(name = "status")
      private Collection<StatusDetailReport> statusDetails = new ArrayList<StatusDetailReport>();
      
      public CategoryReport() {
         
      }
      
      public CategoryReport(Category category) {
         
         this.category = category;
      
      }
      
   }
   
   @XmlAccessorType(XmlAccessType.FIELD)
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @XmlRootElement
   public static class StatusDetailReport {
      
      @XmlAttribute
      private String url;
      @XmlAttribute
      private String origin;
      @XmlAttribute
      private String method;
      @XmlAttribute
      private Integer statusCode;    
      @XmlAttribute
      private String message;   
      @XmlAttribute
      @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
      private LocalDateTime checkingDate;
      @XmlAttribute
      private String contentType;
      @XmlAttribute
      private Long contentLength;
      @XmlAttribute
      private Integer duration;
      @XmlAttribute
      private Integer redirectCount;
      
   }
}

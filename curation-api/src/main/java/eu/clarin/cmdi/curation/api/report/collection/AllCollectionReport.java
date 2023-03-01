package eu.clarin.cmdi.curation.api.report.collection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.clarin.cmdi.curation.api.report.LocalDateTimeAdapter;
import eu.clarin.cmdi.curation.api.report.NamedReport;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AllCollectionReport implements NamedReport {

   @XmlAttribute
   @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
   public LocalDateTime creationTime = LocalDateTime.now();

   @XmlElement(name = "collection")
   private Collection<CollectionReportWrapper> collectionReports = new ArrayList<CollectionReportWrapper>();


   @Override
   public String getName() {

      return this.getClass().getSimpleName();
      
   }

   public void addReport(CollectionReport report) {
      
      this.collectionReports.add(new CollectionReportWrapper((CollectionReport) report));
   
   }

   @XmlRootElement
   @XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
   @RequiredArgsConstructor
   @NoArgsConstructor(force = true)
   public static class CollectionReportWrapper {
      
      private final CollectionReport collectionReport;
      @XmlAttribute
      public String getName() {
         return collectionReport.getName();
      }
      @XmlElement
      public String getReportName() {
         return collectionReport.getName();
      }
      @XmlElement
      public double getScorePercentageProcessable() {
         return collectionReport.scorePercentageProcessable;
      };
      @XmlElement
      public double getScorePercentageAll() {
         return collectionReport.scorePercentageAll;
      };
      @XmlElement
      public long getNumOfFiles() {
         return collectionReport.fileReport.numOfFiles;
      }
      @XmlElement
      public int getNumOfProfiles() {
         return collectionReport.profileReport.totNumOfProfiles;
      }
      @XmlElement
      public int getNumOfLinks() {
         return collectionReport.linkcheckerReport.totNumOfLinks;
      }
      @XmlElement
      public int getNumOfCheckedLinks() {
         return collectionReport.linkcheckerReport.totNumOfCheckedLinks;
      }
      @XmlElement
      public double getRatioOfValidLinks() {
         return collectionReport.linkcheckerReport.ratioOfValidLinks;
      }
      @XmlElement
      public double getAvgNumOfResProxies() {
         return collectionReport.resProxyReport.avgNumOfResProxies;
      }
      @XmlElement
      public long getNumOfResProxies() {
         return collectionReport.resProxyReport.totNumOfResProxies;
      }
      @XmlElement
      public double getRatioOfValidRecords() {
         return collectionReport.xmlValidityReport.avgScoreProcessable;
      }
      @XmlElement
      public double getAvgNumOfEmptyXMLElements() {
         return collectionReport.xmlPopulationReport.avgNumOfXMLEmptyElements;
      }
      @XmlElement
      private double getAvgFacetCoverage() {
         return collectionReport.facetReport.avgScoreProcessable;
      }

      @XmlElementWrapper(name = "facets")
      @XmlElement(name = "facet")
      public Collection<eu.clarin.cmdi.curation.api.report.collection.sec.FacetReport.FacetCollectionStruct> getFacets(){
         return collectionReport.facetReport.facets;
      }
   }
}

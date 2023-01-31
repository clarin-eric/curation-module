package eu.clarin.cmdi.curation.api.report.collection;

import eu.clarin.cmdi.curation.api.report.Detail;
import eu.clarin.cmdi.curation.api.report.LocalDateTimeAdapter;
import eu.clarin.cmdi.curation.api.report.NamedReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.FacetReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.FileReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.HeaderReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.LinkcheckerReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.ResProxyReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.XmlPopulationReport;
import eu.clarin.cmdi.curation.api.report.collection.sec.XmlValidityReport;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */

/**
 * report for one single collection
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionReport implements NamedReport{
   
   @XmlAttribute
   public double aggregatedScore;
   @XmlAttribute
   public double aggregatedMaxScore;
   @XmlAttribute
   public double aggregatedMaxScoreValid;
   @XmlAttribute
   public double scorePercentage;
   @XmlAttribute
   public double avgScore;
   @XmlAttribute
   public double avgScoreValid;   
   @XmlAttribute
   public double minScore = Double.MAX_VALUE;
   @XmlAttribute
   public double maxScore = Double.MAX_VALUE;
   @XmlAttribute
   public double insMaxScore = eu.clarin.cmdi.curation.api.report.instance.CMDInstanceReport.maxScore +1;

   @XmlAttribute
   @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
   public LocalDateTime creationTime = LocalDateTime.now();

   @XmlElement
   public final FileReport fileReport = new FileReport();

   @XmlElement
   public final HeaderReport headerReport = new HeaderReport();

   // ResProxies
   public final ResProxyReport resProxyReport = new ResProxyReport();

   // XMLPopulatedValidator
   @XmlElement
   public final XmlPopulationReport xmlPopulationReport = new XmlPopulationReport();;

   // XMLValidator
   @XmlElement
   public final XmlValidityReport xmlValidityReport = new XmlValidityReport();

   // URL
   @XmlElement
   public final LinkcheckerReport linkcheckerReport = new LinkcheckerReport();

   // Facets
   @XmlElement
   public final FacetReport facetReport = new FacetReport();

   // Invalid Files
   @XmlElementWrapper(name = "recordDetails")
   @XmlElement(name = "record")
   public final Collection<RecordDetail> recordDetails = new ArrayList<RecordDetail>();

   @XmlRootElement
   @RequiredArgsConstructor
   @NoArgsConstructor(force = true)
   public static class RecordDetail {
      @XmlAttribute
      private final String origin;
      @XmlElement(name = "detail")
      private final Collection<Detail> details;
   }


   @Override
   public String getName() {
      return fileReport.provider;
   }
}

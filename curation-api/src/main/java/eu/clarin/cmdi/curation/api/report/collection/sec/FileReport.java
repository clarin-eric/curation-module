/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.cmdi.curation.api.report.collection.sec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileReport {
   @XmlAttribute
   public static final double maxScore = eu.clarin.cmdi.curation.api.report.instance.sec.FileReport.maxScore;
   @XmlAttribute
   public double aggregatedScore = 0.0;
   @XmlAttribute
   public double avgScore;
   @XmlAttribute
   public double avgScoreValid;
   @XmlElement
   public String provider;
   @XmlElement
   public long numOfFiles;
   @XmlElement
   public long numOfValidFiles;
   @XmlElement
   public long size;
   @XmlElement
   public long minFileSize;
   @XmlElement
   public long maxFileSize;
   @XmlElement
   public long avgFileSize;
   
}

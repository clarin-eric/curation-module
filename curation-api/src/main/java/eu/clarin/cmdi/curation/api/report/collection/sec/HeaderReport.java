/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.cmdi.curation.api.report.collection.sec;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;



/**
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HeaderReport {
   @XmlAttribute
   public static double maxScore = 3.0;
   @XmlAttribute
   public double avgScore; 
   @XmlElement
   public int totNumOfProfiles;
   
   @XmlElementWrapper(name = "duplicatedMDSelfLinks")
   private Collection<String> duplicatedMDSelfLink;
   @XmlElementWrapper(name = "profiles")
   @XmlElement(name = "profile")
   public Collection<Profile> profiles = new ArrayList<Profile>();  


   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   @RequiredArgsConstructor
   @NoArgsConstructor(force = true)
   public static class Profile {
      @XmlElement
      public final String id;
      @XmlElement
      public final double score;
      @XmlElement
      public int count = 1;
   }
}

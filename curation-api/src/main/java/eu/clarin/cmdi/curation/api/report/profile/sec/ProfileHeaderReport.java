/**
 * @author Wolfgang Walter SAUER (wowasa) &lt;clarin@wowasa.com&gt;
 *
 */
package eu.clarin.cmdi.curation.api.report.profile.sec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.clarin.cmdi.curation.api.report.ScoreReport;
import eu.clarin.cmdi.curation.pph.ProfileHeader;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ProfileHeaderReport extends ScoreReport {
   
   private final ProfileHeader header;  

   @XmlTransient
   public ProfileHeader getProfileHeader() {
      return header;
   }

   public String getId() {
      return header.getId();
   }


   public String getSchemaLocation() {
      return header.getSchemaLocation();
   }


   public String getName() {
      return header.getName();
   }


   public String getDescription() {
      return header.getDescription();
   }


   public String getCmdiVersion() {
      return header.getCmdiVersion();
   }


   public String getStatus() {
      return header.getStatus();
   }
}

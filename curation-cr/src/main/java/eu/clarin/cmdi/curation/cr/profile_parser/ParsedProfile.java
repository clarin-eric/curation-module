package eu.clarin.cmdi.curation.cr.profile_parser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public record ParsedProfile(
   ProfileHeader header,
   Map<String, CMDINode> xpathNode,
   Map<String, CMDINode> xpathElementNode,
   Map<String, CMDINode> xpathComponentNode
) implements Serializable{
   public Collection<String> getXPaths() {

      return xpathNode.keySet();
   }
}

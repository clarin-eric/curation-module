package eu.clarin.cmdi.curation.subprocessor;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import eu.clarin.cmdi.curation.configuration.CurationConfig;
import eu.clarin.cmdi.curation.cr.CRService;
import eu.clarin.cmdi.curation.cr.ProfileDescription;
import eu.clarin.cmdi.curation.cr.profile_parser.CMDINode;
import eu.clarin.cmdi.curation.report.FacetReport;
import eu.clarin.cmdi.curation.report.FacetReport.Coverage;
import eu.clarin.cmdi.vlo.importer.mapping.FacetsMapping;

class FacetReportCreator {
   
   @Autowired
   private CurationConfig conf;
   @Autowired
   private CRService crService;
	
	public FacetReport createFacetReport(ProfileDescription header, FacetsMapping facetMapping) throws Exception {
	    Map<String, CMDINode> elements = crService.getParsedProfile(header).getElements();
		FacetReport facetReport = new FacetReport();
		facetReport.numOfFacets = conf.getFacets().size();
		facetReport.coverage = new ArrayList<>();
		
		for(String facetName : conf.getFacets()) {
		    Coverage facet = new Coverage();
            facet.name = facetName;
            facet.coveredByProfile = facetMapping.getFacetDefinition(facetName).getPatterns().stream().anyMatch(p -> elements.containsKey(p.getPattern())) || 
                    facetMapping.getFacetDefinition(facetName).getFallbackPatterns().stream().anyMatch(p -> elements.containsKey(p.getPattern()));         
            facetReport.coverage.add(facet);
		}
		
		double numOfCoveredByProfile = facetReport.coverage.stream().filter(f -> f.coveredByProfile).count();
		facetReport.profileCoverage = numOfCoveredByProfile / facetReport.numOfFacets;
		
		return facetReport;		
	}

}

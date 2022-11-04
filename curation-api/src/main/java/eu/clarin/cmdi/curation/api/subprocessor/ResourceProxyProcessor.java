package eu.clarin.cmdi.curation.api.subprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import eu.clarin.cmdi.curation.api.entities.CMDInstance;
import eu.clarin.cmdi.curation.api.report.CMDInstanceReport;
import eu.clarin.cmdi.curation.api.report.Score;
import eu.clarin.cmdi.curation.api.report.CMDInstanceReport.ResProxyReport;
import eu.clarin.cmdi.curation.api.report.CMDInstanceReport.ResourceType;
import eu.clarin.cmdi.vlo.importer.CMDIData;
import eu.clarin.cmdi.vlo.importer.Resource;
import eu.clarin.cmdi.vlo.importer.processor.ValueSet;

@Component
@Scope(value="prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ResourceProxyProcessor extends AbstractSubprocessor {
   
   private ResourceProxyProcessor() {
      
   }

	@Override
	public void process(CMDInstance entity, CMDInstanceReport report) {
	    CMDIData<Map<String, List<ValueSet>>> data = entity.getCMDIData();
	    
		report.resProxyReport = new ResProxyReport();
		
		report.resProxyReport.numOfResProxies = 0;
		report.resProxyReport.numOfResourcesWithMime =  0;
		report.resProxyReport.numOfResProxiesWithReferences = 0;
		report.resProxyReport.resourceType = new ArrayList<ResourceType>();
		
		addResourceType(data.getDataResources(), report);
		addResourceType(data.getLandingPageResources(), report);
		addResourceType(data.getMetadataResources(), report);
		addResourceType(data.getSearchPageResources(), report);
		addResourceType(data.getSearchResources(), report);
			
		report.resProxyReport.percOfResourcesWithMime = report.resProxyReport.numOfResProxies > 0? 
				(double) report.resProxyReport.numOfResourcesWithMime / report.resProxyReport.numOfResProxies : 0;
		report.resProxyReport.percOfResProxiesWithReferences = report.resProxyReport.numOfResProxies > 0? 
				(double) report.resProxyReport.numOfResProxiesWithReferences / report.resProxyReport.numOfResProxies : 0;

		
	}
	
	private void addResourceType(List<Resource> resources, CMDInstanceReport report) {
	    if(resources.isEmpty())
	        return;
	    
	    ResourceType resourceType = new ResourceType();
	    resourceType.type = resources.get(0).getType();
	    resourceType.count = resources.size();
	    
	    for(Resource resource : resources) {
            if(resource.getResourceName() != null && !resource.getResourceName().isEmpty())
                report.resProxyReport.numOfResProxiesWithReferences++;
            if(resource.getMimeType() != null && !resource.getMimeType().isEmpty())
                report.resProxyReport.numOfResourcesWithMime++;
            
            report.resProxyReport.numOfResProxies++;
	    }    
	    report.resProxyReport.resourceType.add(resourceType);  
	}

	public Score calculateScore(CMDInstanceReport report) {
		double score = report.resProxyReport.percOfResourcesWithMime + report.resProxyReport.percOfResProxiesWithReferences;
		return new Score(score, 2.0, "cmd-res-proxy", this.getMessages());		
	}

}

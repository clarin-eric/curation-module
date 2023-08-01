package eu.clarin.cmdi.curation.api.subprocessor.instance;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ximpleware.*;

import eu.clarin.cmdi.curation.api.cache.ProfileReportCache;
import eu.clarin.cmdi.curation.api.conf.ApiConfig;
import eu.clarin.cmdi.curation.api.entity.CMDInstance;
import eu.clarin.cmdi.curation.api.entity.CMDProfile;
import eu.clarin.cmdi.curation.api.report.instance.CMDInstanceReport;
import eu.clarin.cmdi.curation.api.report.instance.sec.InstanceFacetReport;
import eu.clarin.cmdi.curation.api.report.instance.sec.InstanceFacetReport.Coverage;
import eu.clarin.cmdi.curation.api.report.instance.sec.InstanceFacetReport.FacetValueStruct;
import eu.clarin.cmdi.curation.api.report.instance.sec.InstanceFacetReport.ValueNode;
import eu.clarin.cmdi.curation.api.report.profile.sec.ConceptReport;
import eu.clarin.cmdi.curation.api.subprocessor.AbstractSubprocessor;
import eu.clarin.cmdi.curation.cr.CRService;
import eu.clarin.cmdi.curation.cr.exception.NoProfileCacheEntryException;
import eu.clarin.cmdi.curation.cr.profile_parser.CMDINode;
import eu.clarin.cmdi.vlo.importer.processor.ValueSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InstanceFacetProcessor extends AbstractSubprocessor<CMDInstance, CMDInstanceReport> {

   @Autowired
   private ApiConfig conf;
   @Autowired
   private CRService crService;
   @Autowired
   ProfileReportCache profileReportCache;

   @Override
   public void process(CMDInstance instance, CMDInstanceReport report) {

      report.facetReport = new InstanceFacetReport();

      profileReportCache
            .getProfileReport(new CMDProfile(report.profileHeaderReport.getSchemaLocation(),
                  report.profileHeaderReport.getCmdiVersion())).facetReport.coverages
            .forEach(profileCoverage -> report.facetReport.coverages
                  .add(new Coverage(profileCoverage.name, profileCoverage.coveredByProfile)));

      report.facetReport.numOfFacets = report.facetReport.coverages.size();

      Map<String, List<ValueSet>> facetValuesMap = instance.getCmdiData().getDocument();

      // the key of the facetValuesMap is the target facet name
      report.facetReport.coverages.stream().forEach(coverage -> {
         if (coverage.coveredByInstance = facetValuesMap.keySet().contains(coverage.name)) { // initialization and test!
            report.facetReport.numOfFacetsCoveredByInstance++;
         }
         ;
      });

      report.facetReport.percCoveragedByInstance = (double) report.facetReport.numOfFacetsCoveredByInstance
            / report.facetReport.numOfFacets;
      report.facetReport.score = report.facetReport.percCoveragedByInstance;
      report.instanceScore += report.facetReport.score;

      // in case of a single instance analysis we want to know for each node with a
      // value to which facet it is mapped
      if ("instance".equals(conf.getMode())) {

         try {
            Map<String, CMDINode> cmdiNodeMap = crService
                  .getParsedProfile(report.profileHeaderReport.getProfileHeader()).getElements();

            final Map<Integer, List<ValueSet>> indexValueSetMap = facetValuesMap.values() // a List of ValueSet
                  .stream().flatMap(List::stream).collect(Collectors.groupingBy(ValueSet::getVtdIndex));

            VTDGen vtdGen = new VTDGen();
            vtdGen.setDoc(Files.readAllBytes(instance.getPath()));
            vtdGen.parse(false);

            VTDNav nav = vtdGen.getNav();
            AutoPilot ap = new AutoPilot(nav);

            instance.getParsedInstance().getNodes().stream().filter(node -> StringUtils.isNotBlank(node.getValue()))
                  .forEach(node -> {

                     ValueNode valueNode = new ValueNode(node.getXpath(), node.getValue());

                     CMDINode cmdiNode;

                     if ((cmdiNode = cmdiNodeMap.get(node.getXpath().replaceAll("\\[\\d\\]", ""))) != null && cmdiNode.concept != null) {

                        valueNode.concept = new ConceptReport.Concept(cmdiNode.concept);
                     }

                     try {
                        ap.selectXPath(node.getXpath().replaceAll("\\w+:", ""));

                        List<ValueSet> valueSetList;

                        if ((valueSetList = indexValueSetMap.get(ap.evalXPath())) != null) {
                           
                           valueSetList.forEach(valueSet -> {
                              valueNode.facets.add(
                                    new FacetValueStruct(
                                       valueSet.getTargetFacetName(),
                                       valueSet.isDerived(), 
                                       valueSet.isResultOfValueMapping(),
                                       valueSet.getValueLanguagePair().getLeft()
                                    )
                                 );                              
                              });
                        }  
                     }
                     catch (XPathParseException e) {

                        log.error(node.getXpath());
                     }
                     catch (XPathEvalException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                     }
                     catch (NavException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                     }

                     report.facetReport.valueNodes.add(valueNode);
                  });

         }
         catch (NoProfileCacheEntryException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

         }
         catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         catch (EncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         catch (EOFException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         catch (EntityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
         catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
      }
   }
}

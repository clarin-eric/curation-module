package eu.clarin.cmdi.curation.api.processor;

import eu.clarin.cmdi.curation.api.configuration.CurationConfig;
import eu.clarin.cmdi.curation.api.entity.CMDInstance;
import eu.clarin.cmdi.curation.api.exception.SubprocessorException;
import eu.clarin.cmdi.curation.api.report.CMDInstanceReport;
import eu.clarin.cmdi.curation.api.subprocessor.*;
import eu.clarin.cmdi.curation.api.subprocessor.instance.CollectionInstanceFacetProcessor;
import eu.clarin.cmdi.curation.api.subprocessor.instance.FileSizeValidator;
import eu.clarin.cmdi.curation.api.subprocessor.instance.InstanceFacetProcessor;
import eu.clarin.cmdi.curation.api.subprocessor.instance.InstanceHeaderProcessor;
import eu.clarin.cmdi.curation.api.subprocessor.instance.ResourceProxyProcessor;
import eu.clarin.cmdi.curation.api.subprocessor.instance.URLValidator;
import eu.clarin.cmdi.curation.api.subprocessor.instance.XMLValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class CMDInstanceProcessor {

   @Autowired
   private CurationConfig conf;
   @Autowired
   FileSizeValidator fileSizeValidator;
   @Autowired
   InstanceHeaderProcessor instanceHeaderProcessor;
   @Autowired
   ResourceProxyProcessor resourceProxyProcessor;
   @Autowired
   URLValidator urlValidator;
   @Autowired
   XMLValidator xmlValidator;
   @Autowired
   CollectionInstanceFacetProcessor collectionInstanceFacetProcessor;
   @Autowired
   InstanceFacetProcessor instanceFacetProcessor;
   

   public CMDInstanceReport process(CMDInstance record, String parentName) {

      CMDInstanceReport report = new CMDInstanceReport();


         
         Stream.of(
               fileSizeValidator, 
               instanceHeaderProcessor, 
               resourceProxyProcessor, 
               urlValidator, 
               xmlValidator, 
               "collection".equalsIgnoreCase(conf.getMode())?collectionInstanceFacetProcessor:instanceFacetProcessor
               ).forEach(subprocessor -> {
                  try {
                     subprocessor.process(record, report);
                     report.addSegmentScore(subprocessor.calculateScore(report));
                  }
                  catch (SubprocessorException e) {
                     log.debug("can't process file '{}'", record.getPath());
                  }
                  catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               });

      return report;
   }
}
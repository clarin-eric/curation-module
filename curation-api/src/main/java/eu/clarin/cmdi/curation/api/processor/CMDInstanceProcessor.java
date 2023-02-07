package eu.clarin.cmdi.curation.api.processor;

import eu.clarin.cmdi.curation.api.conf.ApiConfig;
import eu.clarin.cmdi.curation.api.entity.CMDInstance;
import eu.clarin.cmdi.curation.api.report.instance.CMDInstanceReport;
import eu.clarin.cmdi.curation.api.subprocessor.AbstractSubprocessor;
import eu.clarin.cmdi.curation.api.subprocessor.instance.*;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CMDInstanceProcessor {
   

   @Autowired
   private ApiConfig conf;
   @Autowired
   private ApplicationContext ctx;
   
   private Collection<AbstractSubprocessor<CMDInstance,CMDInstanceReport>> subprocessors;

   
   @PostConstruct
   private void init() {
      
      subprocessors =   
         Stream.of(FileSizeValidator.class, InstanceHeaderProcessor.class, ResourceProxyProcessor.class, UrlValidator.class, XmlValidator.class)
            .map(abstactSubprocessorClass -> ctx.getBean(abstactSubprocessorClass)).collect(Collectors.toList());
      
      if ("collection".equalsIgnoreCase(conf.getMode()) || "all".equalsIgnoreCase(conf.getMode())) {
         
         subprocessors.add(ctx.getBean(CollectionInstanceFacetProcessor.class));
      
      }
      else {
         
         subprocessors.add(ctx.getBean(InstanceFacetProcessor.class));
         
      }        
   }

   public CMDInstanceReport process(CMDInstance instance){

      final CMDInstanceReport instanceReport = new CMDInstanceReport();


      this.subprocessors
         .stream()
         .takeWhile(p -> instanceReport.isProcessable)
         .forEach(subprocessor -> subprocessor.process(instance, instanceReport));


      return instanceReport;
   }
}

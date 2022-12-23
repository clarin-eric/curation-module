package eu.clarin.cmdi.curation.api.subprocessor.collection;

import eu.clarin.linkchecker.persistence.model.AggregatedStatus;
import eu.clarin.linkchecker.persistence.repository.AggregatedStatusRepository;
import eu.clarin.cmdi.curation.api.conf.ApiConfig;
import eu.clarin.cmdi.curation.api.entity.CMDCollection;
import eu.clarin.cmdi.curation.api.entity.CMDInstance;
import eu.clarin.cmdi.curation.api.report.*;
import eu.clarin.cmdi.curation.api.report.CollectionReport.*;
import eu.clarin.cmdi.curation.api.subprocessor.AbstractSubprocessor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;



/**
 *
 */
@Slf4j
@Component
public class CollectionAggregator extends AbstractSubprocessor<CMDCollection, CollectionReport>{

   @Autowired
   private ApiConfig conf;
   @Autowired
   private ApplicationContext ctx;
   @Autowired
   AggregatedStatusRepository aRep;

   @Transactional
   public void process(CMDCollection collection, CollectionReport collectionReport) {
      
      Score score = new Score("invalid-files", 1.0);
      
      for (String facetName : conf.getFacets()) {
         FacetCollectionStruct facet = new FacetCollectionStruct();
         facet.name = facetName;
         facet.cnt = 0;
         collectionReport.facetReport.facet.add(facet);
      }

      ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(conf.getThreadPoolSize());    

      try {
         Files.walkFileTree(collection.getPath(), new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
               
               collectionReport.fileReport.numOfFiles++;
               
               if(attrs.size() > collectionReport.fileReport.maxFileSize) {
                  collectionReport.fileReport.maxFileSize = attrs.size();
               }
               if(collectionReport.fileReport.minFileSize == 0) {
                  collectionReport.fileReport.minFileSize = attrs.size();
               }
               else if(attrs.size() < collectionReport.fileReport.minFileSize) {
                  collectionReport.fileReport.minFileSize = attrs.size();
               }
               collectionReport.fileReport.size += attrs.size();               
               
               CMDInstance instance = ctx.getBean(CMDInstance.class, filePath, attrs.size(), collectionReport.fileReport.provider);
               
               executor.submit(() -> {
 
                  CMDInstanceReport cmdInstanceReport = instance.generateReport();
                  collectionReport.addReport(cmdInstanceReport);

               }); // end executor.submit            
               
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
               log.error("can't access file '{}'", file);
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

               return FileVisitResult.CONTINUE;
            }      
         });
      }
      catch (IOException e) {
         
         log.error("can't walk through path '{}'", collection.getPath());
         throw new RuntimeException(e);
      
      }

      executor.shutdown();

      while (!executor.isTerminated()) {
         try {
            Thread.sleep(1000);
         }
         catch (InterruptedException ex) {
            log.error("Error occured while waiting for the threadpool to terminate.");
         }
      }

      collectionReport.calculateAverageValues();    
      
      try (Stream<AggregatedStatus> stream = aRep.findAllByProvidergroupName(collectionReport.getName())) {

         stream.forEach(categoryStats -> {
            Statistics xmlStatistics = new Statistics();
            xmlStatistics.avgRespTime = categoryStats.getAvgDuration();
            xmlStatistics.maxRespTime = categoryStats.getMaxDuration();
            xmlStatistics.category = categoryStats.getCategory();
            xmlStatistics.count = categoryStats.getNumber();
            collectionReport.urlReport.totNumOfCheckedLinks += categoryStats.getNumber();

            switch (categoryStats.getCategory()) {
               case Invalid_URL -> collectionReport.urlReport.totNumOfInvalidLinks = categoryStats.getNumber().intValue();
               case Broken -> collectionReport.urlReport.totNumOfBrokenLinks = categoryStats.getNumber().intValue();
               case Undetermined -> collectionReport.urlReport.totNumOfUndeterminedLinks = categoryStats.getNumber().intValue();
               case Restricted_Access ->
               collectionReport.urlReport.totNumOfRestrictedAccessLinks = categoryStats.getNumber().intValue();
               case Blocked_By_Robots_txt ->
               collectionReport.urlReport.totNumOfBlockedByRobotsTxtLinks = (int) categoryStats.getNumber().intValue();
               default -> throw new IllegalArgumentException("Unexpected value: " + categoryStats.getCategory());
            }

            collectionReport.urlReport.statistics.add(xmlStatistics);
         });
      }
      catch (IllegalArgumentException ex) {

         log.error(ex.getMessage());
         
      }
      catch (Exception ex) {

         log.error("couldn't get category statistics for provider group '{}' from database", collectionReport.getName(), ex);
      }

      if (!CMDInstance.duplicateMDSelfLink.isEmpty()) {
         collectionReport.headerReport.duplicatedMDSelfLink = CMDInstance.duplicateMDSelfLink;
      }
      
      CMDInstance.duplicateMDSelfLink.clear();
      CMDInstance.mdSelfLinks.clear();
      
      score.setScore((double) collectionReport.file.size()/collectionReport.fileReport.numOfFiles);
      
      collectionReport.addSegmentScore(score);

   }


}

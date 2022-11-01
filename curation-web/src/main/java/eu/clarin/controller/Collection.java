package eu.clarin.controller;

import eu.clarin.helpers.FileManager;
import eu.clarin.helpers.ResponseManager;
import eu.clarin.main.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/collection")
public class Collection {


    @GetMapping("/{collectionName}")
    public Response getCollection(@PathVariable("collectionName") String collectionName) {

        String[] split = collectionName.split("\\.");
        if (split.length != 2) {
            return ResponseManager.returnError(400, "Collection name must end with either xml or html.");
        }

        String extension = split[1];

        try {

            String location;
            switch (extension) {
                case "xml":
                    location = Configuration.OUTPUT_DIRECTORY + "/xml/collections/";
                    String collectionXML = FileManager.readFile(location + collectionName);
                    return ResponseManager.returnResponse(200, collectionXML, MediaType.TEXT_XML);
                case "html":
                    location = Configuration.OUTPUT_DIRECTORY + "/html/collections/";
                    String collectionHTML = FileManager.readFile(location + collectionName);

                    //replace to put the url based on the server (this way xml and html files are not server url dependent)
                    String xmlLink = Configuration.BASE_URL + "collection/" + split[0] + ".xml";
                    xmlLink = "<a href='"+xmlLink+"'>"+xmlLink+"</a>";
                    collectionHTML = collectionHTML.replaceFirst(Pattern.quote("selfURLPlaceHolder"), xmlLink);

                    return ResponseManager.returnHTML(200, collectionHTML);
                default:
                    return ResponseManager.returnError(400, "Collection name must end with either xml or html.");
            }
        } catch (IOException e) {
            log.error("There was an error reading the collection: " + collectionName);
            return ResponseManager.returnError(404, "The collection " + collectionName + " doesn't exist.");
        }
    }

    @GetMapping("/table")
    public Response getCollectionsTable() {
        try {
            String collections = FileManager.readFile(Configuration.OUTPUT_DIRECTORY + "/html/collections/CollectionsReport.html");

            return ResponseManager.returnHTML(200, collections);
        } catch (IOException e) {
            log.error("Error when reading CollectionsReport.html: ", e);
            return ResponseManager.returnServerError();
        }
    }

    @GetMapping("/tsv")
    public Response getCollectionsTSV() {
        String collectionsTSVPath = Configuration.OUTPUT_DIRECTORY + "/tsv/collections/CollectionsReport.tsv";

        final InputStream fileInStream;
        try {
            fileInStream = new FileInputStream(collectionsTSVPath);
            return ResponseManager.returnFile(200, fileInStream, "text/tab-separated-values", "CollectionsReport.tsv");
        } catch (FileNotFoundException e) {
            log.error("There was an error getting the collectionsReport.tsv file: ", e);
            return ResponseManager.returnServerError();
        }

    }
}
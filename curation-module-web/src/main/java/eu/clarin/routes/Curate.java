package eu.clarin.routes;

import eu.clarin.cmdi.curation.main.CurationModule;
import eu.clarin.cmdi.curation.main.Main;
import eu.clarin.cmdi.curation.report.*;
import eu.clarin.cmdi.curation.utils.FileNameEncoder;
import eu.clarin.curation.linkchecker.httpLinkChecker.HTTPLinkChecker;
import eu.clarin.helpers.FileManager;
import eu.clarin.helpers.ResponseManager;
import eu.clarin.main.Configuration;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.file.Paths;

@Path("/curate")
public class Curate {

    private static final Logger _logger = Logger.getLogger(Curate.class);

    @GET
    @Path("/")
    public Response getInstanceQueryParam(@QueryParam("url-input") String urlStr) {

        if (urlStr == null || urlStr.isEmpty()) {
            return ResponseManager.returnError(400, "Input URL can't be empty.");
        }

        if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) {
            if (urlStr.startsWith("www")) {
                urlStr = "http://" + urlStr;
            } else {
                return ResponseManager.returnError(400, "Given URL is invalid");
            }
        }

        String resultFileName = System.currentTimeMillis() + "_" + FileNameEncoder.encode(urlStr);
        String tempPath = System.getProperty("java.io.tmpdir") + "/" + resultFileName;

        HTTPLinkChecker linkChecker = new HTTPLinkChecker();
        try {
            linkChecker.download(urlStr, new File(tempPath));
        } catch (IOException e) {
            return ResponseManager.returnError(400, "Given URL is invalid");
        }
        try {
            String content = FileManager.readFile(tempPath);
            return curate(content, resultFileName, urlStr);

        } catch (IOException | TransformerException | JAXBException e) {
            _logger.error("There was a problem generating the report: ", e);
            return ResponseManager.returnServerError();
        }
    }

    //this is for drag and drop instance form
    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postInstance(@FormDataParam("file") InputStream fileInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileMetaData) {

        try {
            String uploadFileName = fileMetaData.getFileName();
            if (uploadFileName == null || uploadFileName.isEmpty()) {
                return ResponseManager.returnError(400, "Uploaded file name can't be empty.");
            }

            String content = FileManager.readInputStream(fileInputStream);

            String resultFileName = System.currentTimeMillis() + "_" + uploadFileName;
            String tempPath = System.getProperty("java.io.tmpdir") + "/" + resultFileName;
            FileManager.writeToFile(tempPath, content);

            return curate(content, resultFileName, uploadFileName);
        } catch (IOException | TransformerException | JAXBException e) {
            _logger.error("There was a problem generating the report: ", e);
            return ResponseManager.returnServerError();
        }
    }


    private Response curate(String content, String resultFileName, String fileLocation) throws TransformerException, JAXBException, IOException {
        String tempPath = System.getProperty("java.io.tmpdir") + "/" + resultFileName;

        Report report;
        try {
            CurationModule cm = new CurationModule();
            report = !content.substring(0, 200).contains("xmlns:xs=") ? cm.processCMDInstance(Paths.get(tempPath)) : cm.processCMDProfile(Paths.get(tempPath).toUri().toURL());
        } catch (MalformedURLException e) {
            return ResponseManager.returnError(400, "Input URL is malformed.");

        } catch (Exception e) {
            _logger.error("There was an exception processing the cmd instance: " + e.getMessage());
            return ResponseManager.returnError(400, "There was a problem when processing the input. Please make sure to upload a valid cmd file.");
        }

        if (report instanceof ErrorReport) {
            return ResponseManager.returnError(400, ((ErrorReport) report).error);
        }

        setFileLocation(report, fileLocation);

        resultFileName = resultFileName.split("\\.")[0];
        save(report, resultFileName);

        String resultURL = Configuration.BASE_URL;
        if (report instanceof CMDProfileReport) {
            resultURL = resultURL + "profile/";
        } else if (report instanceof CMDInstanceReport) {
            resultURL = resultURL + "instance/";
        }


        resultURL = resultURL + resultFileName + ".html";

        return ResponseManager.redirect(resultURL);
    }

    //this is needed, because the schema/record location is only known when a url is given.
    //when a file is uploaded, it is not known.
    //fileLocation can't be null because it is checked before
    private void setFileLocation(Report report, String fileLocation) {

        if (report instanceof CMDInstanceReport) {
            if (fileLocation.startsWith("http://") || fileLocation.startsWith("https://")) {
                ((CMDInstanceReport) report).fileReport.location = fileLocation;
            } else {
                ((CMDInstanceReport) report).fileReport.location = "Uploaded file name: " + fileLocation;
            }
        } else if (report instanceof CMDProfileReport) {
            if (fileLocation.startsWith("http://") || fileLocation.startsWith("https://")) {
                ((CMDProfileReport) report).header.setSchemaLocation(fileLocation);
            } else {
                ((CMDProfileReport) report).header.setSchemaLocation("N/A");
            }
        }

    }


    //saves xml report and html representation into the file system
    private void save(Report report, String resultName) throws IOException, JAXBException, TransformerException {

        String xmlPath;
        if (report instanceof CMDProfileReport) {
            xmlPath = Configuration.OUTPUT_DIRECTORY + "/xml/profiles/";
        } else if (report instanceof CMDInstanceReport) {
            xmlPath = Configuration.OUTPUT_DIRECTORY + "/xml/instances/";
        } else {
            throw new IOException("Result wasn't a profile or instance Report. Should not come here.");
        }

        xmlPath = xmlPath + FileNameEncoder.encode(resultName) + ".xml";
        String marshallResult = FileManager.marshall(report);
        FileManager.writeToFile(xmlPath, marshallResult);

        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(Main.class.getResourceAsStream("/xslt/" + report.getClass().getSimpleName() + "2HTML.xsl"));

        Transformer transformer = factory.newTransformer(xslt);

        StringWriter writeBuffer = new StringWriter();

        StreamResult result = new StreamResult(writeBuffer);

        transformer.transform(new JAXBSource(JAXBContext.newInstance(report.getClass()), report), result);

        String htmlPath = null;
        if (report instanceof CMDProfileReport) {
            htmlPath = Configuration.OUTPUT_DIRECTORY + "/html/profiles/";
        } else if (report instanceof CMDInstanceReport) {
            htmlPath = Configuration.OUTPUT_DIRECTORY + "/html/instances/";
        }
        htmlPath = htmlPath + resultName + ".html";
        FileManager.writeToFile(htmlPath, writeBuffer.toString());

    }
}

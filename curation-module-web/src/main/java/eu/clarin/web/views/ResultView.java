package eu.clarin.web.views;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Panel;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import eu.clarin.cmdi.curation.entities.CurationEntity.CurationEntityType;
import eu.clarin.cmdi.curation.main.Configuration;
import eu.clarin.cmdi.curation.main.CurationModule;
import eu.clarin.cmdi.curation.report.CMDInstanceReport;
import eu.clarin.cmdi.curation.report.Report;
import eu.clarin.cmdi.curation.utils.FileNameEncoder;
import eu.clarin.web.MainUI;
import eu.clarin.web.Shared;
import eu.clarin.web.components.LinkButton;
import eu.clarin.web.utils.LinkCheckerStatisticsHelper;
import eu.clarin.web.utils.XSLTTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@DesignRoot
public class ResultView extends Panel implements View {

    private final static Logger _logger = LoggerFactory.getLogger(ResultView.class);

    private enum SourceType {PROFILE_ID, URL, FILE}

    private VerticalLayout sideMenu;

    Label label;
    StreamResource xmlReport;

    XSLTTransformer transformer = new XSLTTransformer();

    public ResultView() {
        setSizeFull();
        label = new Label();
        label.setContentMode(ContentMode.XML);

        setContent(label);

        xmlReport = new StreamResource(null, "report.xml");
        xmlReport.setMIMEType("application/xml");
        xmlReport.setCacheTime(0);

        sideMenu = new VerticalLayout();
        LinkButton export = new LinkButton("Download Report (xml)");
        sideMenu.addComponent(export);

        FileDownloader fileDownloader = new FileDownloader(xmlReport);

        fileDownloader.extend(export);

//		BrowserWindowOpener popup = new BrowserWindowOpener(xmlPopulatedReport);
//		popup.setFeatures("");
//		popup.extend(bXML);	

    }

    @Override
    public void enter(ViewChangeEvent event) {
        int first = event.getParameters().indexOf('/');
        int sec = event.getParameters().indexOf('/', first + 1);

        String curationType = event.getParameters().substring(0, first);
        String source = event.getParameters().substring(first + 1, sec);
        String value = event.getParameters().substring(sec + 1);

        SourceType sourceType = null;
        switch (source) {
            case "id":
                sourceType = SourceType.PROFILE_ID;
                break;
            case "url":
                sourceType = SourceType.URL;
                break;
            case "file":
                sourceType = SourceType.FILE;
                break;
        }

        switch (curationType) {
            case "instance":
                ((MainUI) getUI()).setCustomMenu(sideMenu);
                curate(CurationEntityType.INSTANCE, sourceType, value);
                break;
            case "profile":
                ((MainUI) getUI()).setCustomMenu(sideMenu);
                curate(CurationEntityType.PROFILE, sourceType, value);
                break;
            case "collection":
                ((MainUI) getUI()).setCustomMenu(sideMenu);
                curate(CurationEntityType.COLLECTION, sourceType, value);
                break;
        }


    }


    private void curate(CurationEntityType curationType, SourceType sourceType, String input) {
        Report r = null;
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            switch (curationType) {
                case INSTANCE:
                    String filePath;
                    switch (sourceType) {
                        case FILE:
                            filePath = Configuration.OUTPUT_DIRECTORY + "/instances/" + input;
                            if (Files.exists(Paths.get(filePath))) {
                                byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
                                result = new ByteArrayOutputStream(byteArray.length);
                                result.write(byteArray, 0, byteArray.length);
                            } else {
                                Path cmdiFile = Paths.get(System.getProperty("java.io.tmpdir"), input);
                                r = new CurationModule().processCMDInstance(cmdiFile);
                                ((CMDInstanceReport) r).fileReport.location = cmdiFile.getFileName().toString();

                                //save it to file
//                                r.toXML(Files.newOutputStream(Paths.get(filePath)));

//                                r.toXML(result);
                            }
                            break;
                        case URL:
                            filePath = Configuration.OUTPUT_DIRECTORY + "/instances/" + FileNameEncoder.encode(input) + ".xml";
                            if (Files.exists(Paths.get(filePath))) {
                                byte[] byteArray = Files.readAllBytes(Paths.get(filePath));
                                result = new ByteArrayOutputStream(byteArray.length);
                                result.write(byteArray, 0, byteArray.length);
                            } else {
                                r = new CurationModule().processCMDInstance(new URL(input));

                                //save it to file
//                                r.toXML(Files.newOutputStream(Paths.get(filePath)));

//                                r.toXML(result);
                            }
                            break;
                    }

                    label.setValue(transformer.transform(curationType, result.toString()));

                    //I set the value of result into temp because otherwise jvm complains:
                    //Variable 'result' is accessed from within inner class, needs to be declared final
                    final ByteArrayOutputStream temp = result;

                    xmlReport.setStreamSource(new StreamSource() {
                        @Override
                        public InputStream getStream() {
                            return new ByteArrayInputStream(temp.toByteArray());
                        }
                    });

                    break;
                case PROFILE:
                    switch (sourceType) {
                        case PROFILE_ID:
                            r = new CurationModule().processCMDProfile(input);
                            break;
                        case URL:
                            r = new CurationModule().processCMDProfile(new URL(input));
                            break;
                    }

//                    r.toXML(result);

                    label.setValue(transformer.transform(curationType, result.toString()));

                    //I set the value of result into temp2 because otherwise jvm complains:
                    //Variable 'result' is accessed from within inner class, needs to be declared final
                    final ByteArrayOutputStream temp2 = result;

                    xmlReport.setStreamSource(new StreamSource() {
                        @Override
                        public InputStream getStream() {
                            return new ByteArrayInputStream(temp2.toByteArray());
                        }
                    });

                    break;

                case COLLECTION:
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(Configuration.COLLECTION_HTML_DIRECTORY)) {
                        for (Path path : ds) {
                            if (path.getFileName().toString().equals(input + ".html")) {

                                byte[] output = Files.readAllBytes(path);

                                label.setContentMode(ContentMode.HTML);
                                label.setValue(new String(output));

                            }
                        }
                    }

                    r = Shared.getCollectionReport(input);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    r.toXML(out);

                    xmlReport.setStreamSource(new StreamSource() {
                        @Override
                        public InputStream getStream() {
                            return new ByteArrayInputStream(out.toByteArray());
                        }
                    });

                    break;


            }


        } catch (Exception e) {
            e.printStackTrace();

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String msg = "Error while assesing " + curationType.toString().toLowerCase() + " from " + input + "!\n"
                    + errors.toString();
            label.setValue("<pre>" + msg + "</pre>");
        }
    }
}

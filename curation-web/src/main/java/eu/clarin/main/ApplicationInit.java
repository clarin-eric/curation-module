package eu.clarin.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.clarin.cmdi.curation.api.exception.UncaughtExceptionHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class ApplicationInit implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInit.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());//to log uncaught exceptions
            ServletContext servletContext = servletContextEvent.getServletContext();
            Configuration.init(servletContext);

        } catch (IOException e) {
            logger.error("There was a problem loading the properties file:" + e.getMessage());
        }
    }
}

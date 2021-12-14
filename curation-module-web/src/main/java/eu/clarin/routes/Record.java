package eu.clarin.routes;

import eu.clarin.helpers.FileManager;
import eu.clarin.helpers.ResponseManager;
import eu.clarin.main.Configuration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

//this route is to make records available to view
@Path("/record")
public class Record {

    @GET
    @Path("/{filepath : .+}")
    public Response handleView(@PathParam("filepath") String filePath) {
        try {
        	// the next three lines assure that the path is a sub-path of RECORDS_PATH
        	java.nio.file.Path path = Paths.get(Configuration.DATA_DIRECTORY, filePath).toRealPath(LinkOption.NOFOLLOW_LINKS);
        	if(!path.startsWith(Configuration.DATA_DIRECTORY))
        		return ResponseManager.returnError(404, "Path not permitted.");
        	
            String file = FileManager.readFile(path.toString());
            return ResponseManager.returnResponse(200, file, MediaType.TEXT_XML);

        } catch (IOException e) {
            return ResponseManager.returnError(404, "Requested file doesn't exist.");
        }

    }

}
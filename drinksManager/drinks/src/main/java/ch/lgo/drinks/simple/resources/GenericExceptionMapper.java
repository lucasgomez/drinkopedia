package ch.lgo.drinks.simple.resources;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import ch.lgo.drinks.simple.exceptions.ErrorMessage;

/**
 * @author codingpedia : http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/
 *
 */
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
    	Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
    	
    	//TODO Adapt this stupid code to provide something useful... 
        ErrorMessage errorMessage = new ErrorMessage();     
        setHttpStatus(ex, errorMessage);
        errorMessage.setCode(Status.NOT_FOUND.getStatusCode());
        errorMessage.setMessage(ex.getMessage());
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        errorMessage.setDeveloperMessage(errorStackTrace.toString());
        errorMessage.setLink("This should be a link to doc");

        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void setHttpStatus(Throwable ex, ErrorMessage errorMessage) {
        if(ex instanceof WebApplicationException ) {
            errorMessage.setStatus(((WebApplicationException)ex).getResponse().getStatus());
        } else {
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); //defaults to internal server error 500
        }
    }
    
}

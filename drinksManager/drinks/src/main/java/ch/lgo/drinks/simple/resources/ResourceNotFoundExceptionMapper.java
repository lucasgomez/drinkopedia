package ch.lgo.drinks.simple.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<Throwable> {

    public ResourceNotFoundExceptionMapper() {
    }
    
    //TODO Add some kind of logger for exceptions
    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof ResourceNotFoundException) {
            return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Too much beer hurts cognitive abilities").build();
        }
    }

}

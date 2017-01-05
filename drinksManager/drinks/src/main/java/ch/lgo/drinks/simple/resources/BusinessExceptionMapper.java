package ch.lgo.drinks.simple.resources;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.lgo.drinks.simple.exceptions.BusinessException;
import ch.lgo.drinks.simple.exceptions.ErrorMessage;
import ch.lgo.drinks.simple.exceptions.NoContentFoundException;
import ch.lgo.drinks.simple.exceptions.ResourceNotFoundException;

@Provider
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    public BusinessExceptionMapper() {
    }
    
    //TODO Add some kind of logger for exceptions
    @Override
    public Response toResponse(BusinessException exception) {
        if (exception instanceof ResourceNotFoundException) {
            return Response.status(Status.NOT_FOUND)
                        .entity(new ErrorMessage(exception))
                        .build();
        } else if (exception instanceof NoContentFoundException) {
            return Response.noContent().entity(new ErrorMessage(exception)).build();
        } else {
            //Generic response
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new ErrorMessage(exception)).build();
        }
    }

}

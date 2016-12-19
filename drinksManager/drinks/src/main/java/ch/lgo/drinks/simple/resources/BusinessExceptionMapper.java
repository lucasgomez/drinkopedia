package ch.lgo.drinks.simple.resources;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ch.lgo.drinks.simple.exceptions.BusinessException;
import ch.lgo.drinks.simple.exceptions.ErrorMessage;

@Provider
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF8"})
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    public BusinessExceptionMapper() {
    }
    
    //TODO Add some kind of logger for exceptions
    //TODO More refined exception catching, maybe runtime VS app ex or whatever
    @Override
    public Response toResponse(BusinessException exception) {
        return Response.status(Status.NOT_FOUND)
                    .entity(new ErrorMessage(exception))
                    .type(MediaType.APPLICATION_JSON).
                    build();
    }

}

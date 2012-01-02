package ca.fieber.api.exceptionmappers;

import ca.fieber.api.representations.ErrorMessage;
import com.google.inject.Singleton;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * An ExceptionMapper for IllegalArgumentException that renders an HTTP 400 (BadRequest) as an ErrorMessage.
 *
 * @author cfieber
 */
@Provider
@Singleton
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(exception.getMessage()))
                .build();
    }
}

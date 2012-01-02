package ca.fieber.api.exceptionmappers;

import ca.fieber.api.representations.ErrorMessage;
import com.google.inject.Singleton;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.NoSuchElementException;

/**
 * An ExceptionMapper for NoSuchElementException that renders an HTTP 404 (Not Found) as an ErrorMessage.
 *
 * @author cfieber
 */
@Provider
@Singleton
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {

    @Override
    public Response toResponse(NoSuchElementException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorMessage(exception.getMessage()))
                .build();
    }
}

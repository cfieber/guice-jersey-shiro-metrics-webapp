package ca.fieber.api.exceptionmappers;

import ca.fieber.api.representations.ErrorMessage;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * IllegalArgumentExceptionMapperTest.
 *
 * @author cfieber
 */
public class IllegalArgumentExceptionMapperTest {
    
    @Test
    public void testExceptionMapper() {
        IllegalArgumentExceptionMapper mapper = new IllegalArgumentExceptionMapper();
        
        IllegalArgumentException exception = new IllegalArgumentException("kaboom");
        Response response = mapper.toResponse(exception);
        assertEquals(ErrorMessage.class, response.getEntity().getClass());
        assertEquals(exception.getMessage(), ((ErrorMessage) response.getEntity()).getMessage());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}

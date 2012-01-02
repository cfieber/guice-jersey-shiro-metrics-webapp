package ca.fieber.api.exceptionmappers;

import ca.fieber.api.representations.ErrorMessage;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
/**
 * NoSuchElementExceptionMapper.
 *
 * @author cfieber
 */
public class NoSuchElementExceptionMapperTest {
    
    @Test
    public void testExceptionMapper() {
        NoSuchElementExceptionMapper mapper = new NoSuchElementExceptionMapper();
        
        NoSuchElementException exception = new NoSuchElementException("kerplow");
        Response resp = mapper.toResponse(exception);
        assertEquals(ErrorMessage.class, resp.getEntity().getClass());
        assertEquals(exception.getMessage(), ((ErrorMessage) resp.getEntity()).getMessage());
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }
}

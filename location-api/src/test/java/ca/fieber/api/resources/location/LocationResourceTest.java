package ca.fieber.api.resources.location;

import ca.fieber.api.module.LocationApiModule;
import ca.fieber.api.repositories.location.LocationRepository;
import ca.fieber.api.representations.location.Location;
import ca.fieber.testing.JettyGuiceTestContainer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * LocationResourceTest.
 *
 * @author cfieber
 */
public class LocationResourceTest {
    
    private static JettyGuiceTestContainer container;
    
    @BeforeClass
    public static void createTestContainer() throws Exception {
        container = new JettyGuiceTestContainer(Collections.singleton(new LocationApiModule()));
        container.start();
    }
    
    @AfterClass
    public static void shutdownContainer() throws Exception {
        container.stop();
    }
    
    @Test
    public void testGetLocation() throws Exception {
        LocationRepository repository = container.getInjector().getInstance(LocationRepository.class);

        String id = repository.create(new Location("foo", 0, 0)).getId();
        HttpResponse httpResponse = container.execute(new HttpGet("/location/" + id));
        assertEquals(HttpURLConnection.HTTP_OK, httpResponse.getStatusLine().getStatusCode());
        HttpEntity entity = httpResponse.getEntity();
        assertEquals(ContentType.APPLICATION_JSON.getMimeType(), ContentType.get(entity).getMimeType());
        String content = EntityUtils.toString(entity);
        ObjectMapper om = new ObjectMapper();
        Map<String, ?> response = om.readValue(content, new TypeReference<Map<String, ?>>() {});
        assertEquals(id, response.get("id"));
    }

    @Test
    public void testGetLocation_NotFound() throws Exception {
        HttpResponse httpResponse = container.execute(new HttpGet("/location/DoesNotExist"));
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testCreateLocation() throws Exception {
        Map<String, Object> location = new HashMap<String, Object>();
        location.put("name", "test-location");
        location.put("longitude", -122.4d);
        location.put("latitude", 48.5d);

        ObjectMapper om = new ObjectMapper();
        HttpPost createRequest = new HttpPost("/location");
        createRequest.setEntity(new ByteArrayEntity(om.writeValueAsBytes(location), ContentType.APPLICATION_JSON));
        
        HttpResponse httpResponse = container.execute(createRequest);
        assertEquals(HttpURLConnection.HTTP_CREATED, httpResponse.getStatusLine().getStatusCode());
        String locationUri = httpResponse.getFirstHeader("Location").getValue();
        assertNotNull(locationUri);
        assertTrue(URI.create(locationUri).isAbsolute());
        Map<String, ?> response = om.readValue(EntityUtils.toString(httpResponse.getEntity()), new TypeReference<Map<String, ?>>() {});
        assertEquals("test-location", response.get("name"));
        assertEquals(-122.4d, response.get("longitude"));
        assertEquals(48.5d, response.get("latitude"));
    }

    @Test
    public void testDeleteLocation() throws Exception {
        LocationRepository repository = container.getInjector().getInstance(LocationRepository.class);

        String id = repository.create(new Location("foo", 0, 0)).getId();
        HttpResponse httpResponse = container.execute(new HttpDelete("/location/" + id));
        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testDeleteLocation_NotFound() throws Exception {
        HttpResponse httpResponse = container.execute(new HttpDelete("/location/ThisDoesNotExist"));
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testUpdateLocation() throws Exception {
        LocationRepository repository = container.getInjector().getInstance(LocationRepository.class);

        String id = repository.create(new Location("foo", 0, 0)).getId();
        
        Map<String, Object> location = new HashMap<String, Object>();
        location.put("id", id);
        location.put("name", "test-location");
        location.put("longitude", -122.4d);
        location.put("latitude", 48.5d);

        ObjectMapper om = new ObjectMapper();
        HttpPut updateRequest = new HttpPut("/location/" + id);
        updateRequest.setEntity(new ByteArrayEntity(om.writeValueAsBytes(location), ContentType.APPLICATION_JSON));
        
        HttpResponse httpResponse = container.execute(updateRequest);
        assertEquals(HttpURLConnection.HTTP_OK, httpResponse.getStatusLine().getStatusCode());

        Map<String, ?> response = om.readValue(EntityUtils.toString(httpResponse.getEntity()), new TypeReference<Map<String, ?>>() {});
        assertEquals("test-location", response.get("name"));
        assertEquals(-122.4d, response.get("longitude"));
        assertEquals(48.5d, response.get("latitude"));
    }

    @Test
    public void testUpdateLocation_NotFound() throws Exception {
        Map<String, Object> location = new HashMap<String, Object>();
        location.put("id", "DoesNotExist");
        location.put("name", "test-location");
        location.put("longitude", -122.4d);
        location.put("latitude", 48.5d);

        ObjectMapper om = new ObjectMapper();
        HttpPut updateRequest = new HttpPut("/location/DoesNotExist");
        updateRequest.setEntity(new ByteArrayEntity(om.writeValueAsBytes(location), ContentType.APPLICATION_JSON));

        HttpResponse httpResponse = container.execute(updateRequest);
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }
}

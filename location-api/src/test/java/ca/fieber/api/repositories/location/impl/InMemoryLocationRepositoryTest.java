package ca.fieber.api.repositories.location.impl;

import ca.fieber.api.representations.location.Location;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
/**
 * InMemoryLocationRepositoryTest.
 *
 * @author cfieber
 */
public class InMemoryLocationRepositoryTest {
    
    private static final double DELTA = 0.0001d;
    
    @Test
    public void testCreate() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        
        Location loc = new Location("test", -122.3, 48.5);
        Location created = repo.create(loc);
        
        assertNotNull(created.getId());
        assertEquals(loc.getLongitude(), created.getLongitude(), DELTA);
        assertEquals(loc.getLatitude(), created.getLatitude(), DELTA);
        assertEquals(loc.getName(), created.getName());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreate_WithId() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        
        Location loc = new Location("This isn't right...", "test", -122.3, 48.5);
        repo.create(loc);
    }
    
    @Test
    public void testGet() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        Location loc = new Location("loc", 0, 0);
        
        Location created = repo.create(loc);
        Location got = repo.get(created.getId());
        assertSame(created, got);
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testGet_NotFound() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        repo.get("kaboom");
    }
    
    @Test
    public void testUpdate() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        Location loc = new Location("loc", 0, 0);
        
        Location created = repo.create(loc);
        Location update = new Location(created.getId(), "A New Name", -122.5, 48.5);
        
        repo.update(update);
        Location got = repo.get(update.getId());
        assertEquals(update.getName(), got.getName());
        assertEquals(update.getLongitude(), got.getLongitude(), DELTA);
        assertEquals(update.getLatitude(), got.getLatitude(), DELTA);
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testUpdate_NotFound() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        
        Location loc = new Location(UUID.randomUUID().toString(), "loc", 0, 0);
        repo.update(loc);
    }
    
    @Test
    public void testRemove() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        Location loc = new Location("loc", 0, 0);
        
        Location created = repo.create(loc);
        repo.remove(created.getId());
        
        try {
            repo.get(created.getId());
            fail("Expected NoSuchElementException after delete");
        } catch (NoSuchElementException expected) {
            // expected
        }
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testRemove_NotFound() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();

        repo.remove("my head asplode");
    }
    
    @Test
    public void testList() {
        InMemoryLocationRepository repo = new InMemoryLocationRepository();
        final int testDataSize = 20;

        List<String> createdIds = new ArrayList<String>(testDataSize); 
        for (int i = 0; i < testDataSize; i++) {
            createdIds.add(repo.create(new Location("loc" + i, 0, 0)).getId());
        }
        Collections.sort(createdIds);
        
        List<Location> locations = repo.list(0, testDataSize);
        assertEquals(testDataSize, locations.size());
        
        locations = repo.list(testDataSize, 100);
        assertTrue(locations.isEmpty());
        
        locations = repo.list(testDataSize / 2, testDataSize);
        assertEquals(locations.size(), testDataSize / 2);
        
        locations = repo.list(0, testDataSize / 2);
        assertEquals(locations.size(), testDataSize / 2);
        for (int i = 0; i < locations.size(); i++) {
            assertEquals(createdIds.get(i), locations.get(i).getId());
        }
    }
}

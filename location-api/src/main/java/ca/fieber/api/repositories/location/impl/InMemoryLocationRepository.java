package ca.fieber.api.repositories.location.impl;

import ca.fieber.api.repositories.location.LocationRepository;
import ca.fieber.api.representations.location.Location;
import com.google.inject.Singleton;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.CounterMetric;
import com.yammer.metrics.core.HistogramMetric;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.core.TimerMetric;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * A simple (non durable!) implementation of LocationRepository that just keeps locations in a ConcurrentMap in memory.
 *
 * @author cfieber
 */
@Singleton
public class InMemoryLocationRepository implements LocationRepository {
    /**
     * The storage for this location repository, initially empty.
     */
    private final ConcurrentMap<String, Location> locations = new ConcurrentHashMap<String, Location>();

    /**
     * A Counter of the number of requests to this repository for non existant Locations.
     */
    private final CounterMetric notFoundCounter = Metrics.newCounter(InMemoryLocationRepository.class, "not-found");

    /**
     * A Counter of the number of invalid requests to this repository.
     */
    private final CounterMetric badRequestCounter = Metrics.newCounter(InMemoryLocationRepository.class, "bad-request");

    /**
     * A Histogram of the sizes of lists returned from this repository.
     */
    private final HistogramMetric listSizeMetric = Metrics.newHistogram(InMemoryLocationRepository.class, "list-size");

    /**
     * A Timer on sorting the list results from this repository.
     */
    private final TimerMetric listSortMetric = Metrics.newTimer(InMemoryLocationRepository.class, "list-sort-timer", TimeUnit.NANOSECONDS, TimeUnit.SECONDS);


    /**
     * A Comparator for Location that orders by id.
     */
    private static class LocationComparator implements Comparator<Location> {
        public int compare(Location l1, Location l2) {
            return l1.getId().compareTo(l2.getId());
        }
    }

    @Override
    public List<Location> list(int startIndex, int pageSize) {
        if (startIndex >= locations.size()) {
            listSizeMetric.update(0);
            return Collections.emptyList();
        }
        
        List<Location> locationList = new ArrayList<Location>(locations.values());
        final TimerContext timerContext = listSortMetric.time();
        try {
            Collections.sort(locationList, new LocationComparator());
        } finally {
            timerContext.stop();
        }
        
        final int fromIndex = Math.min(startIndex, locationList.size());
        final int toIndex = Math.min(startIndex + pageSize, locationList.size());
        listSizeMetric.update(toIndex - fromIndex);
        return locationList.subList(fromIndex, toIndex);
    }
    
    @Override
    public Location get(String id) throws NoSuchElementException {
        Location location = locations.get(id);
        if (location == null) {
            notFoundCounter.inc();
            throw new NoSuchElementException("Location: " + id);
        }
        return location;
    }

    @Override
    public void update(Location location) {
        Location replaced = locations.replace(location.getId(), location);
        if (replaced == null) {
            notFoundCounter.inc();
            throw new NoSuchElementException("Location: " + location.getId());
        }
    }

    @Override
    public void remove(String id) {
        Location removed = locations.remove(id);
        if (removed == null) {
            notFoundCounter.inc();
            throw new NoSuchElementException("Location: " + id);
        }
    }

    @Override
    public Location create(Location location) {
        if (location.getId() != null) {
            badRequestCounter.inc();
            throw new IllegalArgumentException("Location already has an id: " + location.getId());
        }
        Location withId = new Location(UUID.randomUUID().toString(), location);
        Location existing = locations.putIfAbsent(withId.getId(), withId);
        if (existing != null) {
            throw new IllegalStateException("Map already contains location with id: " + existing.getId());
        }
        return withId;
    }
}

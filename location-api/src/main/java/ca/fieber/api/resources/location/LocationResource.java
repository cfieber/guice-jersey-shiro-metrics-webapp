package ca.fieber.api.resources.location;

import ca.fieber.api.repositories.location.LocationRepository;
import ca.fieber.api.representations.location.Location;
import ca.fieber.api.representations.location.LocationList;
import com.google.inject.Inject;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.core.TimerMetric;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Exposes REST methods for Locations.
 *
 * @author cfieber
 */
@Path("/location")
public class LocationResource {

    /**
     * Page size to use when generating LocationLists.
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * LocationRepository for this LocationResource.
     */
    private final LocationRepository locationRepository;

    /**
     * A timer for get requests for specific Locations.
     */
    private final TimerMetric getTimer = Metrics.newTimer(getClass(), "GET /location/{id}");

    /**
     * A timer for Location list requests.
     */
    private final TimerMetric listTimer = Metrics.newTimer(getClass(), "GET /location");

    /**
     * A timer for Location creation.
     */
    private final TimerMetric createTimer = Metrics.newTimer(getClass(), "POST /location");

    /**
     * A timer for Location updates.
     */
    private final TimerMetric updateTimer = Metrics.newTimer(getClass(), "PUT /location/{id}");

    /**
     * A timer for Location deletes.
     */
    private final TimerMetric deleteTimer = Metrics.newTimer(getClass(), "DELETE /location/{id}");

    /**
     * Constructs a new LocationResource with the provided LocationRepository.
     *
     * @param locationRepository the LocationRepository for this LocationResource.
     */
    @Inject
    public LocationResource(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Gets a list of Locations starting from the specified start index.
     *
     * @param uriInfo Context parameter for URI creation
     * @param startIndex the start index for the Location list, defaults to 0.
     * @return a LocationList containing the Locations and a link to additional Locations if applicable.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response listLocations(@Context UriInfo uriInfo, @QueryParam("start-index") @DefaultValue("0") int startIndex) {
        final TimerContext timerContext = listTimer.time();
        try {
            List<Location> locations = locationRepository.list(startIndex, DEFAULT_PAGE_SIZE + 1);
            boolean hasNextPage = locations.size() > DEFAULT_PAGE_SIZE;
            URI nextPage = null;
            if (hasNextPage) {
                nextPage = uriInfo.getBaseUriBuilder()
                        .path(LocationResource.class)
                        .path(LocationResource.class, "listLocations")
                        .queryParam("start-index", startIndex + DEFAULT_PAGE_SIZE)
                        .build();
            }
            LocationList list = new LocationList(locations.subList(0, Math.min(DEFAULT_PAGE_SIZE, locations.size())), nextPage);
            return Response.ok(list).build();
        } finally {
            timerContext.stop();
        }
    }

    /**
     * Creates a new Location.
     *
     * <p>The provided Location must not have an existing ID.</p>
     *
     * @param uriInfo Context parameter for URI creation
     * @param location the Location to create.
     * @return the newly created Location and the authoritative URI linking to its representation.
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createLocation(@Context UriInfo uriInfo, Location location) {
        final TimerContext timerContext = createTimer.time();
        try {
            Location created = locationRepository.create(location);
            return Response.created(uriInfo.getBaseUriBuilder()
                    .path(LocationResource.class)
                    .path(LocationResource.class, "getLocation")
                    .build(created.getId()))
                    .entity(created)
                    .build();
        } finally {
            timerContext.stop();
        }
    }

    /**
     * Gets the representation of a Location.
     *
     * @param id the ID of the Location
     * @return the Location with the specified ID
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getLocation(@PathParam("id") String id) {
        final TimerContext timerContext = getTimer.time();
        try {
            Location location = locationRepository.get(id);
            return Response.ok(location).build();
        } finally {
            timerContext.stop();
        }
    }

    /**
     * Updates the specified Location.
     *
     * @param id the ID of the Location to update
     * @param location the updated values for the Location
     * @return the updated Location
     */
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateLocation(@PathParam("id") String id, Location location) {
        final TimerContext timerContext = updateTimer.time();
        try {
            if (!id.equals(location.getId())) {
                throw new IllegalArgumentException("Invalid location id for this URI: " + location.getId());
            }
            locationRepository.update(location);
            return Response.ok(location).build();
        } finally {
            timerContext.stop();
        }
    }

    /**
     * Deletes the specified Location.
     *
     * @param id the ID of the Location to delete
     * @return HTTP 204 (No Content)
     */
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteLocation(@PathParam("id") String id) {
        final TimerContext timerContext = deleteTimer.time();
        try {
            locationRepository.remove(id);
            return Response.noContent().build();
        } finally {
            timerContext.stop();
        }
    }
}

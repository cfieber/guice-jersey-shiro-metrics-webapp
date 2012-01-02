package ca.fieber.api.repositories.location;

import ca.fieber.api.representations.location.Location;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Interface for a repository of Locations.
 *
 * @author cfieber
 */
public interface LocationRepository {

    /**
     * Retrieves Locations from the repository.
     *
     * <p>If the start index is beyond the size of the repository, an empty list is returned.</p>

     * @param startIndex the index (0 based) into the repository from which to retrieve locations.
     * @param pageSize the maximum number of Locations to return
     * @return the Locations, never null
     */
    List<Location> list(int startIndex, int pageSize);

    /**
     * Retrieves a Location by id
     * @param id the id of the Location
     * @return the Location for the id
     * @throws NoSuchElementException if there is no Location with the specified id in the repository
     */
    Location get(String id) throws NoSuchElementException;

    /**
     * Updates the Location
     * @param location the Location to update
     * @throws NoSuchElementException if a Location doesn't already exist in the repository for the provided Location's id
     */
    void update(Location location) throws NoSuchElementException;

    /**
     * Removes a Location by id
     * @param id the id of the Location
     * @throws NoSuchElementException if there is no Location with the specified id in the repository
     */
    void remove(String id) throws NoSuchElementException;

    /**
     * Creates a new Location with the provided template Location.
     *
     * <p>The provided Location must not already have an id.</p>
     * @param location the template Location containing values for the new Location
     * @return a new Location with the template Location's values and an id assigned by the repository
     */
    Location create(Location location);
}

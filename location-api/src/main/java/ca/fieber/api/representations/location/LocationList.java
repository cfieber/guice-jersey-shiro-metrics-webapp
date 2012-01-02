package ca.fieber.api.representations.location;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * A list of Locations.
 *
 * @author cfieber
 */
@XmlRootElement(name = "location-list")
public class LocationList {

    /**
     * The locations for this LocationList.
     */
    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    private List<Location> locations;

    /**
     * A link to the next page of locations for this LocationList.
     */
    @XmlAttribute(name = "next-page")
    private URI nextPage;

    /**
     * No-arg constructor for JAXB compatibility.
     */
    @SuppressWarnings("unused")
    public LocationList() {
        this(null, null);
    }

    /**
     * Constructs a LocationList with the provided locations and nextPage link.
     *
     * @param locations the locations for this LocationList
     * @param nextPage the link to the next page of Locations for this LocationList
     */
    public LocationList(List<Location> locations, URI nextPage) {
        this.locations = locations == null ? Collections.<Location>emptyList() : locations;
        this.nextPage = nextPage;
    }

    /**
     * Gets the locations for this LocationList.
     *
     * @return the locations for this LocationList
     */
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * Gets the link to the next page of locations for this LocationList.
     *
     * @return the link to the next page of locations for this LocationList
     */
    public URI getNextPage() {
        return nextPage;
    }
}

package ca.fieber.api.representations.location;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Location representation.
 *
 * @author cfieber
 */
@XmlRootElement
public class Location {

    /**
     * ID for this Location.
     */
    @XmlAttribute
    private String id;

    /**
     * Name for this Location.
     */
    @XmlElement
    private String name;

    /**
     * Longitude for this location in decimal degrees.
     */
    @XmlElement
    private double longitude;

    /**
     * Latitude for this location in decimal degrees.
     */
    @XmlElement
    private double latitude;

    /**
     * No-arg constructor for JAXB compatibility.
     */
    @SuppressWarnings("unused")
    public Location() {
        this(null, null, 0.0d, 0.0d);
    }

    /**
     * Constructs a new Location with the provided values and no ID suitable for creation in a LocationRepository.
     *
     * @param name the name of the Location
     * @param longitude the Longitude of the Location
     * @param latitude the Latitude of the Location
     */
    public Location(String name, double longitude, double latitude) {
        this(null, name, longitude, latitude);
    }

    /**
     * Constructs a new Location with the provided values.
     *
     * @param id the ID of the Location
     * @param name the name of the Location
     * @param longitude the longitude of the Location
     * @param latitude the latitude of the Location
     */
    public Location(String id, String name, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Constructs a new Location with an assigned ID and copies values from a template Location.
     *
     * @param id the ID of the Location
     * @param copy the Location from which to copy values.
     */
    public Location(String id, Location copy) {
        this(id, copy.getName(), copy.getLongitude(), copy.getLatitude());
    }

    /**
     * Gets the ID of this Location.
     *
     * @return the ID of this Location
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of this Location.
     *
     * @return the name of this Location
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the longitude of this Location.
     *
     * @return the longitude of this Location.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the latitude of this Location.
     *
     * @return the latitude of this Location.
     */
    public double getLatitude() {
        return latitude;
    }
}

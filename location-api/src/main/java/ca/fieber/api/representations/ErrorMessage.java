package ca.fieber.api.representations;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Conveys an error response.
 *
 * @author cfieber
 */
@XmlRootElement(name = "error-message")
public class ErrorMessage {

    /**
     * The error message
     */
    @XmlAttribute
    String message;

    /**
     * No-arg constructor for JAXB compatibility.
     */
    @SuppressWarnings("unused")
    public ErrorMessage() {
        this(null);
    }

    /**
     * Constructs a new ErrorMessage with the specified message.
     *
     * @param message the message for the ErrorMessage
     */
    public ErrorMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the message for this ErrorMessage.
     *
     * @return the message for this ErrorMessage
     */
    public String getMessage() {
        return message;
    }
}

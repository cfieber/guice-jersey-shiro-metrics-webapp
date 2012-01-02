package ca.fieber.api.module;

import ca.fieber.api.exceptionmappers.IllegalArgumentExceptionMapper;
import ca.fieber.api.exceptionmappers.NoSuchElementExceptionMapper;
import ca.fieber.api.representations.ErrorMessage;
import ca.fieber.api.representations.location.Location;
import ca.fieber.api.representations.location.LocationList;
import ca.fieber.api.resources.location.LocationResource;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.List;

/**
 * Configures the JAX-RS components for the Location API.
 *
 * @author cfieber
 */
class ResourcesModule extends JerseyServletModule {
    @Override
    protected void configureServlets() {
        bind(LocationResource.class);
        bind(NoSuchElementExceptionMapper.class);
        bind(IllegalArgumentExceptionMapper.class);
        bind(JAXBContextProvider.class);

        serve("/*").with(GuiceContainer.class);
    }

    /**
     * Provides the list of classes that the JAXBContext should be aware of.
     *
     * @return the list of classes for the JAXBContext.
     */
    @Provides
    @RepresentationClasses        
    List<Class<?>> representationTypes() {
        final Class<?>[] representationTypes = {Location.class, LocationList.class, ErrorMessage.class};
        return Arrays.asList(representationTypes);
    }

    /**
     * Customizes the JSONConfiguration for JAXB parsing/rendering of JSON.
     *
     * @return the JSONConfiguration to use
     */
    @Provides
    JSONConfiguration jsonConfiguration() {
        return JSONConfiguration.natural().build();
    }

    /**
     * Provides a JAXBContext configured for the specified JSONConfiguration and representation classes.
     *
     * @param jsonConfiguration the JSONConfiguration for the JAXBContext
     * @param types the representation classes that the JAXBContext will be configured to use
     * @return a JAXBContext with the JSONConfiguration for the provided classes
     */
    @Provides
    @Singleton
    JAXBContext jaxbContext(JSONConfiguration jsonConfiguration, @RepresentationClasses List<Class<?>> types) {
        try {
            return new JSONJAXBContext(jsonConfiguration, types.toArray(new Class<?>[types.size()]));
        } catch (JAXBException jaxbe) {
            throw new ProvisionException("Failed to create JAXBContext", jaxbe);
        }
    }
}

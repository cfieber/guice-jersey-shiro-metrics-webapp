package ca.fieber.api.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

/**
 * A ContextResolver for JAXBContext that accepts the JAXBContext as an injectable constructor argument.
 *
 * @author cfieber
 */
@Provider
@Singleton
public class JAXBContextProvider implements ContextResolver<JAXBContext> {

    /**
     * The JAXBContext for this ContextResolver.
     */
    private final JAXBContext context;

    /**
     * Constructs a new JAXBContextProvider with the provided context.
     *
     * @param context the JAXBContext for this JAXBContextProvider
     */
    @Inject
    public JAXBContextProvider(JAXBContext context) {
        this.context = context;
    }

    @Override
    public JAXBContext getContext(Class<?> type) {
        return context;
    }
}

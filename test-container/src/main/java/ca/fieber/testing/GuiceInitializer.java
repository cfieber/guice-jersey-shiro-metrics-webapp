package ca.fieber.testing;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * A GuiceServletContextListener that uses a pre-configured Injector.
 *
 * @author cfieber
 */
class GuiceInitializer extends GuiceServletContextListener {

    /**
     * The Injector for this GuiceInitializer.
     */
    private final Injector injector;

    /**
     * Constructs a new GuiceInitializer with the provided Injector
     *
     * @param injector the Injector for this GuiceInitializer
     */
    public GuiceInitializer(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected Injector getInjector() {
        return injector;
    }

}

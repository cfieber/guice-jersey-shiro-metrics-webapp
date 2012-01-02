package ca.fieber.api.module;

import com.google.inject.AbstractModule;

/**
 * Public module that assembles components for the Location API.
 *
 * @author cfieber
 */
public class LocationApiModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new RepositoriesModule());
        install(new ResourcesModule());
    }
}

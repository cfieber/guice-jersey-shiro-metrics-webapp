package ca.fieber.api.module;

import ca.fieber.api.repositories.location.LocationRepository;
import ca.fieber.api.repositories.location.impl.InMemoryLocationRepository;
import com.google.inject.AbstractModule;

/**
 * Assembles the components for the Location API.
 *
 * @author cfieber
 */
class RepositoriesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LocationRepository.class).to(InMemoryLocationRepository.class);
    }
}

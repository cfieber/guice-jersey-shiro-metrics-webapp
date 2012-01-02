package ca.fieber.security.config;

import com.google.inject.servlet.ServletModule;
import org.apache.shiro.guice.web.GuiceShiroFilter;

/**
 * ServletModule that installs the Shiro filters.
 *
 * @author cfieber
 */
class ShiroFilterModule extends ServletModule {

    @Override
    protected void configureServlets() {
        filter("*").through(GuiceShiroFilter.class);
    }
}

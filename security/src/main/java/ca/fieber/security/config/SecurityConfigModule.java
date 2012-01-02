package ca.fieber.security.config;

import com.google.inject.AbstractModule;

import javax.servlet.ServletContext;

/**
 * Configures Security settings for the application.
 *
 * @author cfieber
 */
public class SecurityConfigModule extends AbstractModule {

    private final ServletContext servletContext;
    private final String securityFilterPathSpec;
    
    public SecurityConfigModule(ServletContext servletContext, String securityFilterPathSpec) {
        this.servletContext = servletContext;
        this.securityFilterPathSpec = securityFilterPathSpec;
    }

    @Override
    protected void configure() {
        install(new ShiroFilterModule());
        install(new ShiroConfigurationModule(servletContext, securityFilterPathSpec));
    }
}

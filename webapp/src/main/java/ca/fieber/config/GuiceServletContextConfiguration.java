package ca.fieber.config;

import ca.fieber.api.module.LocationApiModule;
import ca.fieber.security.config.SecurityConfigModule;
import ca.fieber.statuscode.HttpMetricsModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * ServletContextListener that creates the Guice Injector.
 *
 * @author cfieber
 */
public class GuiceServletContextConfiguration extends GuiceServletContextListener {
    private ServletContext servletContext;

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new HttpMetricsModule(), new SecurityConfigModule(servletContext, "/location/**"), new LocationApiModule());
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }
}

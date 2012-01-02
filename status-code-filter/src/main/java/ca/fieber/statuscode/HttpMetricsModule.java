package ca.fieber.statuscode;

import com.google.inject.servlet.ServletModule;

/**
 * Configures the filters for HTTP Response Status Code metrics.
 *
 * @author cfieber
 */
public class HttpMetricsModule extends ServletModule {

    @Override
    protected void configureServlets() {
        filter("/*").through(HttpResponseStatusCodeMetricsFilter.class);
    }
}

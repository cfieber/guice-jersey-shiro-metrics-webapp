package ca.fieber.statuscode;

import com.google.inject.Singleton;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.HistogramMetric;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet Filter that tracks HTTP status codes grouped by class of status code.
 *
 * Each class of status codes is tracked by a <code>HistogramMetric</code>.  Metric names are as follows:
 * <ul>
 * <li>Informational (100 - 199) - http-status-codes-http-1xx</li>
 * <li>Successful (200 - 299) - http-status-codes-http-2xx</li>
 * <li>Redirection (300 - 399) - http-status-codes-http-3xx</li>
 * <li>Client Error (400 - 499) - http-status-codes-http-4xx</li>
 * <li>Server Error (500 - 599) - http-status-codes-http-5xx</li>
 * <li>Any other value - http-status-codes-invalid</li>
 * </ul>
 *
 * @author cfieber
 */
@Singleton
public class HttpResponseStatusCodeMetricsFilter implements Filter {

    /**
     * Base name for metric names.
     */
    private static final String METRIC_NAME_BASE = "http-status-codes-";

    /**
     * Holds the metric for each response category in the index <code>floor(statusCode / 100)</code>
     * as well as the metric for invalid status codes in index 0.
     */
    private final HistogramMetric[] statusCodeMetrics = new HistogramMetric[6];

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) resp;
        chain.doFilter(req, httpResponse);
        int statusCode = httpResponse.getStatus();
        getMetricForStatusCode(statusCode).update(statusCode);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        for (int i = 0; i < statusCodeMetrics.length; i++) {
            statusCodeMetrics[i] = Metrics.newHistogram(HttpResponseStatusCodeMetricsFilter.class, METRIC_NAME_BASE + getNameForIndex(i));
        }
    }
    
    @Override
    public void destroy() {
        for (int i = 0; i < statusCodeMetrics.length; i++) {
            Metrics.removeMetric(HttpResponseStatusCodeMetricsFilter.class, METRIC_NAME_BASE + getNameForIndex(i));
            statusCodeMetrics[i] = null;
        }
    }

    /**
     * Looks up the appropriate HistogramMetric for the provided status code.
     *
     * @param statusCode The HTTP status code for which to find a <code>HistogramMetric</code>
     * @return the <code>HistogramMetric</code> for the status code, never <code>null</code>.
     */
    HistogramMetric getMetricForStatusCode(int statusCode) {
        return statusCodeMetrics[getIndexForStatusCode(statusCode)];
    }

    /**
     * Finds the array index in statusCodeMetrics for the HTTP status code.
     *
     * @param statusCode the HTTP status code for which to find the array index
     * @return the array index for the HTTP status code, always a valid index in <code>statusCodeMetrics</code>.
     */
    int getIndexForStatusCode(int statusCode) {
        int index = (int) Math.floor(statusCode / 100.0d);
        if (isInvalidIndex(index)) {
            return 0;
        }
        return index;
    }

    /**
     * Determines whether the provided index represents an invalid category of HTTP status codes.
     *
     * @param index the index to check
     * @return true iff the index is less than 1 (HTTP 1xx Informational) or greater than 5 (HTTP 5xx Server Error).
     */
    boolean isInvalidIndex(int index) {
        return index < 1 || index >= statusCodeMetrics.length;
    }

    /**
     * Gets a descriptive name for the index.
     *
     * @param index the index for which to get a name
     * @return if the index represents a valid status code, returns a name in the form <code>"http-" + index + "xx"</code>
     *         otherwise returns "invalid".
     */
    String getNameForIndex(int index) {
        if (isInvalidIndex(index)) {
            return "invalid";
        }
        
        return "http-" + index + "xx";
    }
}

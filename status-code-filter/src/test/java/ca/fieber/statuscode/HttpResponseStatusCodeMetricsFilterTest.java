package ca.fieber.statuscode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * HttpResponseStatusCodeMetricsFilterTest.
 *
 * @author cfieber
 */
public class HttpResponseStatusCodeMetricsFilterTest {
    
    HttpResponseStatusCodeMetricsFilter filter;
    
    @Before
    public void createFilter() {
        FilterConfig config = createStrictMock(FilterConfig.class);
        replay(config);
        if (filter == null) {
            filter = new HttpResponseStatusCodeMetricsFilter();
            try {
                filter.init(config);
            } catch (ServletException se) {
                fail(se.getMessage());
            }
        }
    }

    @After
    public void destroyFilter() {
        if (filter != null) {
            filter.destroy();
        }
        filter = null;
    }

    @Test
    public void testGetNameForIndex() {
        assertEquals("invalid", filter.getNameForIndex(0));
        assertEquals("http-1xx", filter.getNameForIndex(1));
        assertEquals("http-2xx", filter.getNameForIndex(2));
        assertEquals("http-3xx", filter.getNameForIndex(3));
        assertEquals("http-4xx", filter.getNameForIndex(4));
        assertEquals("http-5xx", filter.getNameForIndex(5));
        assertEquals("invalid", filter.getNameForIndex(6));
        assertEquals("invalid", filter.getNameForIndex(-1));
        assertEquals("invalid", filter.getNameForIndex(42));
    }

    @Test
    public void testGetMetricForStatusCode() {
        //HTTP 1xx codes:
        assertSame(filter.getMetricForStatusCode(SC_CONTINUE), filter.getMetricForStatusCode(SC_SWITCHING_PROTOCOLS));
        assertNotSame(filter.getMetricForStatusCode(SC_CONTINUE), filter.getMetricForStatusCode(SC_CREATED));
        
        //HTTP 2xx codes:
        assertSame(filter.getMetricForStatusCode(SC_OK), filter.getMetricForStatusCode(SC_CREATED));
        assertNotSame(filter.getMetricForStatusCode(SC_OK), filter.getMetricForStatusCode(SC_MOVED_PERMANENTLY));
        
        //HTTP 3xx codes:
        assertSame(filter.getMetricForStatusCode(SC_MOVED_PERMANENTLY), filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY));
        assertNotSame(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY), filter.getMetricForStatusCode(SC_BAD_REQUEST));
        
        //HTTP 4xx codes:
        assertSame(filter.getMetricForStatusCode(SC_BAD_REQUEST), filter.getMetricForStatusCode(SC_FORBIDDEN));
        assertNotSame(filter.getMetricForStatusCode(SC_BAD_REQUEST), filter.getMetricForStatusCode(SC_INTERNAL_SERVER_ERROR));
        
        //HTTP 5xx codes:
        assertSame(filter.getMetricForStatusCode(SC_INTERNAL_SERVER_ERROR), filter.getMetricForStatusCode(SC_SERVICE_UNAVAILABLE));
        assertNotSame(filter.getMetricForStatusCode(SC_SERVICE_UNAVAILABLE), filter.getMetricForStatusCode(42));
        
        //invalid codes:
        assertSame(filter.getMetricForStatusCode(42), filter.getMetricForStatusCode(612));
        assertNotSame(filter.getMetricForStatusCode(612), filter.getMetricForStatusCode(SC_CONTINUE));
    }

    @Test
    public void testFilterChainUpdatesMetric() throws ServletException, IOException {
        ServletRequest req = createStrictMock(ServletRequest.class);
        HttpServletResponse resp = createStrictMock(HttpServletResponse.class);
        resp.sendRedirect("/foo/bar");
        expectLastCall();
        expect(resp.getStatus()).andReturn(SC_MOVED_TEMPORARILY);
        replay(req, resp);
        
        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                ((HttpServletResponse) response).sendRedirect("/foo/bar");
            }
        };
        
        assertEquals(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY).count(), 0);
        filter.doFilter(req, resp, chain);
        assertEquals(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY).count(), 1);
        assertEquals(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY).min(), Integer.valueOf(SC_MOVED_TEMPORARILY).doubleValue(), 0.001d);
        assertEquals(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY).max(), Integer.valueOf(SC_MOVED_TEMPORARILY).doubleValue(), 0.001d);
        assertEquals(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY).mean(), Integer.valueOf(SC_MOVED_TEMPORARILY).doubleValue(), 0.001d);
        assertEquals(filter.getMetricForStatusCode(SC_MOVED_TEMPORARILY).stdDev(), 0.0d, 0.001d);
    }
}

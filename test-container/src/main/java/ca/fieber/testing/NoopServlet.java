package ca.fieber.testing;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that does nothing but serves as a valid end point so Jetty will route requests
 * through the installed Filters.
 *
 * @author cfieber
 */
class NoopServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ServletException("not supported");
    }
}

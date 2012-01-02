package ca.fieber.testing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A container for testing Guice modules in a servlet environment.
 *
 * <p>An embedded Jetty server is started on an ephemeral port the GuiceFilter
 * configured to intercept all requests.</p>
 *
 * <p>The Injector created for the Guice container is exposed to allow access to
 * the container components.</p>
 *
 * <p>Additionally, a commons HttpClient is created to allow execution of requests
 * against the container.  The executed requests can be created with relative URIs
 * and will be properly resolved to the correct HttpHost for the container.</p>
 * @author cfieber
 */
public class JettyGuiceTestContainer {

    /**
     * The base URI of the server.
     */
    private final URI baseUri;

    /**
     * The embedded Jetty server.
     */
    private final Server server;

    /**
     * The Injector
     */
    private final Injector injector;

    /**
     * The HttpHost for the server.
     */
    private final HttpHost httpHost;

    /**
     * The HttpClient for requests against the Server.
     */
    private final HttpAsyncClient client;

    /**
     * Constructs a new JettyGuiceTestContainer with the specified Modules.
     *
     * @param modules the Modules for this container
     * @throws IOException if there is a problem creating the Container.
     */
    public JettyGuiceTestContainer(Collection<? extends Module> modules) throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverSocket.close();
        injector = Guice.createInjector(modules);
        Server server = new Server(port);
        baseUri = URI.create("http://localhost:" + port + "/");
        httpHost = new HttpHost("localhost", port, "http");
        ServletContextHandler handler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        handler.addEventListener(new GuiceInitializer(injector));
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        handler.addServlet(NoopServlet.class, "/*");
        this.server = server;
        this.client = new DefaultHttpAsyncClient();
    }

    /**
     * Gets the base URI for this container.
     * @return the base URI for this container
     */
    public URI getBaseUri() {
        return baseUri;
    }

    /**
     * Gets the Injector for this container.
     *
     * @return the Injector for this container
     */
    public Injector getInjector() {
        return injector;
    }

    /**
     * Starts the container.
     *
     * @throws Exception if there is a problem starting the container
     */
    public void start() throws Exception {
        server.start();
        client.start();
    }

    /**
     * Stops the container.
     *
     * @throws Exception if there is a problem stopping the container
     */
    public void stop() throws Exception {
        server.stop();
        client.shutdown();
    }

    /**
     * Executes an HttpRequest and blocks until it is complete.
     *
     * <p>Note that the URI for the HttpRequest can be relative and will be
     * correctly executed against this container.</p>
     *
     * @param request the request to execute
     * @return the HttpResponse for the request
     * @throws InterruptedException if the request is interrupted
     * @throws ExecutionException if there is an exception executing the request
     */
    public HttpResponse execute(HttpRequest request) throws InterruptedException, ExecutionException {
        return executeAsync(request).get();
    }

    /**
     * Executes an HttpRequest asynchronously.
     *
     * @param request the request to execute
     * @return a Future from which to obtain the HttpResponse
     */
    public Future<HttpResponse> executeAsync(HttpRequest request) {
        return executeAsync(request, null);
    }

    /**
     * Executes an HttpRequest asynchronously.
     *
     * @param request the request to execute
     * @param callback a FutureCallback for the HttpResponse
     * @return a Future from which to obtain the HttpResponse
     */
    public Future<HttpResponse> executeAsync(HttpRequest request, FutureCallback<HttpResponse> callback) {
        return client.execute(httpHost, request, callback);
    }
}

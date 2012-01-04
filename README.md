This project was a learning exercise for a few different frameworks I've not had a good excuse to try yet.

* Dependency injection via [Google Guice](http://code.google.com/p/google-guice/)
* Security via [Apache Shiro](http://shiro.apache.org/)
* REST via [Jersey](http://jersey.java.net/)
* Monitoring/Metrics via [Yammer Metrics](http://metrics.codahale.com/)

The application exposes a dummy RESTful API for locations that supports both JSON and XML.

The API is configured to require SSL and basic authentication with a dummy realm.

The REST resource is instrumented to monitor request timings.

Additionally there is a servlet filter that tracks the distribution of HTTP response status codes.

The project is built with Maven and shouldn't require any special configuration. There are several Maven modules:

* location-api - the REST API 
* security - Apache Shiro configuration
* status-code-filter - The servlet filter that tracks HTTP response status codes
* test-container - A testing utility for testing Guice servlets in Jetty
* webapp - A deployable web application that puts it all together

Running the webapp:
===================

Security
--------

The security configuration is set to require SSL, and is configured to expect that to be on port 8443.  At some point I will
externalize the port configuration, but for now you would need to either configure SSL on 8443 (this is the default for Tomcat
which I was using to play with the API), or change the port number in the ShiroConfigurationModule in the security Maven module.

The authentication realm is set to a Shiro IniRealm, so usernames and passwords are configured in the shiro.ini file in the webapp.
Out of the box you can use cfieber/pass to log in (but please don't go steal my bank accounts now that you know my password!)

HTTP Status Code Logging
------------------------

The HTTP Status Code servlet filter uses the Yammer metrics library to track status code responses from the REST API.  The metrics
library exposes each of the metrics as an MBean, so connecting to the application with jconsole or VisualVM will give you a live
view on the state of the application.

Additionally, there is a servlet context listener that will periodically dump the HTTP Status Code metrics to CSV.  The output location is configurable
via a context parameter in web.xml, and defaults to a /tmp/<ISO 8601 Timestamp>/ folder.

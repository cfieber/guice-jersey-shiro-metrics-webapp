This project was a learning exercise for a few different frameworks I've not had a good excuse to try yet.

* Dependency injection via [Google Guice](http://code.google.com/p/google-guice/)
* Security via [Apache Shiro](http://shiro.apache.org/)
* REST via [Jersey](http://jersey.java.net/)
* Monitoring/Metrics via [Yammer Metrics](http://metrics.codahale.com/)

The application exposes a dummy RESTful API for locations that supports both JSON and XML.
The API is configured to require SSL and basic authentication with a dummy realm.
The REST resource is instrumented to monitor request timings.
Additionally there is a servlet filter that tracks the distribution of HTTP response status codes.



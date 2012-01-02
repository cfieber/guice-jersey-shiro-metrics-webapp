package ca.fieber.statuscode.logging;

import ca.fieber.statuscode.HttpResponseStatusCodeMetricsFilter;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.reporting.AbstractPollingReporter;
import com.yammer.metrics.reporting.CsvReporter;
import com.yammer.metrics.util.MetricPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * A ServletContextListener that enables logging of metrics in CSV format on
 * initialization of the ServletContext, and shuts down logging on destruction
 * of the ServletContext.
 *
 * @author cfieber
 */
public class MetricsLoggingServletContextListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private AbstractPollingReporter metricsReporter;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        String metricsLocation = sce.getServletContext().getInitParameter("metrics-location");
        File f = new File(metricsLocation);
        if (f.exists()) {
            if (!(f.isDirectory() && f.canWrite())) {
                logger.error(f.getAbsolutePath() + " is not writable or is not a directory, metrics logging disabled");
                return;
            }
        } else {
            if (!f.mkdirs()) {
                logger.error("Unable to create directory " + f.getAbsolutePath() + ", metrics logging disabled");
                return;
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        File metricsDir = new File(f, dateFormat.format(new Date()));
        if (!metricsDir.mkdir()) {
            logger.error("Unable to create directory " + metricsDir.getAbsolutePath() + ", metrics logging disabled");
            return;
        }
        
       
        final MetricName httpResponseStatusCodeMetrics = new MetricName(HttpResponseStatusCodeMetricsFilter.class, "http-response-codes");
        MetricPredicate predicate = new MetricPredicate() {
            public boolean matches(MetricName name, Metric metric) {
                return name.getGroup().equals(httpResponseStatusCodeMetrics.getGroup());
            }
        };

        try {
            metricsReporter = new CsvReporter(metricsDir, Metrics.defaultRegistry(), predicate);
            metricsReporter.start(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            metricsReporter = null;
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (metricsReporter != null) {
            metricsReporter.shutdown();
            metricsReporter = null;
        }
        Metrics.shutdown();
    }
}

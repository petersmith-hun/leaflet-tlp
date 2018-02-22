package hu.psprog.leaflet.tlp.core.service;

import hu.psprog.leaflet.tlp.core.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;

/**
 * Log processing operations service interface.
 *
 * @author Peter Smith
 */
public interface LogProcessingService {

    /**
     * Returns a page of logs based on given {@link LogRequest}.
     *
     * @param logRequest {@link LogRequest} to return logs for
     * @return LogEventPage object containing a list of logs with paging information
     */
    LogEventPage getLogs(LogRequest logRequest);

    /**
     * Stores given {@link LoggingEvent}.
     *
     * @param loggingEvent {@link LoggingEvent} to store
     */
    void storeLog(LoggingEvent loggingEvent);
}

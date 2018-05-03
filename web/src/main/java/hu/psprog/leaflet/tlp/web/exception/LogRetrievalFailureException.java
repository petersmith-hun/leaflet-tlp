package hu.psprog.leaflet.tlp.web.exception;

import hu.psprog.leaflet.tlp.api.domain.LogRequest;

/**
 * Exception to throw when log retrieval request cannot be fulfilled.
 *
 * @author Peter Smith
 */
public class LogRetrievalFailureException extends Exception {

    private static final String MESSAGE = "Failed to process log request [%s]";

    public LogRetrievalFailureException(LogRequest logRequest, Throwable cause) {
        super(String.format(MESSAGE, logRequest), cause);
    }
}

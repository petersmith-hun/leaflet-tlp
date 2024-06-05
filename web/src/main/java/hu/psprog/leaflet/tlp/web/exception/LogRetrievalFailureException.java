package hu.psprog.leaflet.tlp.web.exception;

import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlql.exception.DSLParserException;

/**
 * Exception to throw when log retrieval request cannot be fulfilled.
 *
 * @author Peter Smith
 */
public class LogRetrievalFailureException extends Exception {

    private static final String MESSAGE = "Failed to process log request [%s]";
    private static final String MESSAGE_DSL_ERROR = "Failed to process log request [%s]: %s";

    public LogRetrievalFailureException(String logRequest, DSLParserException cause) {
        super(String.format(MESSAGE_DSL_ERROR, logRequest, cause.getMessage()), cause);
    }

    public LogRetrievalFailureException(String logRequest, Throwable cause) {
        super(String.format(MESSAGE, logRequest), cause);
    }

    public LogRetrievalFailureException(LogRequest logRequest, Throwable cause) {
        super(String.format(MESSAGE, logRequest), cause);
    }
}

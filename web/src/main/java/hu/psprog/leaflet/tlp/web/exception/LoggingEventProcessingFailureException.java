package hu.psprog.leaflet.tlp.web.exception;

/**
 * Exception to throw when received log event cannot be processed.
 *
 * @author Peter Smith
 */
public class LoggingEventProcessingFailureException extends Exception {

    private static final String MESSAGE = "Log event cannot be processed";

    public LoggingEventProcessingFailureException(Throwable cause) {
        super(MESSAGE, cause);
    }
}

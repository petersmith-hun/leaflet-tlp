package hu.psprog.leaflet.tlp.web.rest.controller;

import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.service.LogProcessingService;
import hu.psprog.leaflet.tlp.web.exception.LogRetrievalFailureException;
import hu.psprog.leaflet.tlp.web.exception.LoggingEventProcessingFailureException;
import hu.psprog.leaflet.tlp.web.exception.model.ErrorMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Log processor controller.
 *
 * @author Peter Smith
 */
@RestController
public class LogsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsController.class);
    private static final String UNEXPECTED_EXCEPTION_OCCURRED = "Unexpected exception occurred";

    static final String PATH_LOGS = "/logs";
    static final String PATH_V2_LOGS = "/v2/logs";

    private final LogProcessingService logProcessingService;

    @Autowired
    public LogsController(LogProcessingService logProcessingService) {
        this.logProcessingService = logProcessingService;
    }

    /**
     * GET /logs
     * Returns stored log messages. {@link LogRequest} parameters can be provided as GET parameters:
     *
     * Paging:
     *  - page:             page number (starts with 1) - defaults to 1
     *  - limit:            maximum number of items on a page - defaults to 50
     *  - orderBy:          order results by (TIMESTAMP|CONTENT|LEVEL) - defaults to TIMESTAMP
     *  - orderDirection:   order results in (ASC|DESC) direction - defaults to DESC
     *
     * Filtering:
     *  - source:           optional, filter log events to given source application
     *  - level:            optional, filter log events to given level
     *  - from:             optional, filter log events after given date (in YYYY-MM-dd format)
     *  - to:               optional, filter log events before given date (in YYYY-MM-dd format)
     *  - content:          optional, filter log events containing given substring (searches in log message, exception message and stacktrace)
     *
     * @param logRequest paging and filtering parameters parsed as {@link LogRequest} object
     * @return paged list of log events returned for given {@link LogRequest} with HTTP status 200
     * @throws LogRetrievalFailureException when {@link LogRequest} cannot be processed
     * @deprecated this endpoint is part of the old TLP API and will be removed in the next major version
     */
    @GetMapping(path = PATH_LOGS)
    @Deprecated(forRemoval = true)
    public ResponseEntity<LogEventPage> getLogs(LogRequest logRequest) throws LogRetrievalFailureException {

        try {
            return ResponseEntity
                    .ok(logProcessingService.getLogs(logRequest));
        } catch (Exception exc) {
            throw new LogRetrievalFailureException(logRequest, exc);
        }
    }

    /**
     * POST /v2/logs
     * Returns stored messages. Expects a valid TLQL query string.
     *
     * @param logRequest TLQL query string
     * @return paged list of log events returned for given TLQL query with HTTP status 200
     * @throws LogRetrievalFailureException when TLQL query cannot be processed
     */
    @PostMapping(path = PATH_V2_LOGS)
    public ResponseEntity<LogEventPage> getLogs(@RequestBody String logRequest) throws LogRetrievalFailureException {

        try {
            return ResponseEntity
                    .ok(logProcessingService.getLogs(logRequest));
        } catch (Exception exc) {
            throw new LogRetrievalFailureException(logRequest, exc);
        }
    }

    /**
     * POST /logs
     * Stores given {@link LoggingEvent}.
     *
     * @param loggingEvent {@link LoggingEvent} object to store
     * @return empty response with HTTP status 201
     * @throws LoggingEventProcessingFailureException when received {@link LoggingEvent} cannot be processed
     */
    @PostMapping(path = PATH_LOGS)
    public ResponseEntity<Void> storeLog(@RequestBody LoggingEvent loggingEvent) throws LoggingEventProcessingFailureException {

        try {
            logProcessingService.storeLog(loggingEvent);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        } catch (Exception exc) {
            throw new LoggingEventProcessingFailureException(exc);
        }
    }

    /**
     * Exception handler for {@link LogRetrievalFailureException}.
     *
     * @param exception exception object
     * @return exception message with HTTP status 400
     */
    @ExceptionHandler(LogRetrievalFailureException.class)
    ResponseEntity<ErrorMessageResponse> retrievalFailureHandler(LogRetrievalFailureException exception) {

        LOGGER.error("Failed to retrieve logs", exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildExceptionMessageForResponse(exception));
    }

    /**
     * Exception handler for {@link LoggingEventProcessingFailureException}.
     *
     * @param exception exception object
     * @return exception message with HTTP status 409
     */
    @ExceptionHandler(LoggingEventProcessingFailureException.class)
    ResponseEntity<ErrorMessageResponse> eventProcessingFailureHandler(LoggingEventProcessingFailureException exception) {

        LOGGER.error("Failed to process log event", exception);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildExceptionMessageForResponse(exception));
    }

    /**
     * Default exception handler.
     *
     * @param exception exception that has been thrown
     * @return exception message with HTTP status 500
     */
    @ExceptionHandler
    ResponseEntity<ErrorMessageResponse> defaultExceptionHandler(Exception exception) {

        LOGGER.error(UNEXPECTED_EXCEPTION_OCCURRED, exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildExceptionMessageForResponse());
    }

    private ErrorMessageResponse buildExceptionMessageForResponse() {

        return ErrorMessageResponse.getBuilder()
                .withMessage(UNEXPECTED_EXCEPTION_OCCURRED)
                .build();
    }

    private ErrorMessageResponse buildExceptionMessageForResponse(Exception exception) {

        return ErrorMessageResponse.getBuilder()
                .withMessage(exception.getMessage())
                .build();
    }
}

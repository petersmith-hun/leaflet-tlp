package hu.psprog.leaflet.tlp.web.rest.controller;

import hu.psprog.leaflet.tlp.core.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.service.LogProcessingService;
import hu.psprog.leaflet.tlp.web.exception.LogRetrievalFailureException;
import hu.psprog.leaflet.tlp.web.exception.LoggingEventProcessingFailureException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link LogsController}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class LogsControllerTest {

    private static final LogRequest LOG_REQUEST = new LogRequest();
    private static final LogEventPage LOG_EVENT_PAGE = LogEventPage.getBuilder().build();
    private static final LoggingEvent LOGGING_EVENT = LoggingEvent.getBuilder().build();
    private static final String MESSAGE_KEY = "message";
    private static final String LOG_RETRIEVAL_FAILURE_MESSAGE = String.format("Failed to process log request [%s]", LOG_REQUEST);
    private static final String LOGGING_EVENT_PROCESSING_FAILURE_MESSAGE = "Log event cannot be processed";
    private static final String UNEXPECTED_EXCEPTION_MESSAGE = "Unexpected exception occurred";

    @Mock
    private LogProcessingService logProcessingService;

    @InjectMocks
    private LogsController logsController;

    @Test
    public void shouldGetLogs() throws LogRetrievalFailureException {

        // given
        given(logProcessingService.getLogs(LOG_REQUEST)).willReturn(LOG_EVENT_PAGE);

        // when
        ResponseEntity<LogEventPage> result = logsController.getLogs(LOG_REQUEST);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(result.getBody(), equalTo(LOG_EVENT_PAGE));
    }

    @Test(expected = LogRetrievalFailureException.class)
    public void shouldGetLogsThrowLogRetrievalException() throws LogRetrievalFailureException {

        // given
        doThrow(RuntimeException.class).when(logProcessingService).getLogs(LOG_REQUEST);

        // when
        logsController.getLogs(LOG_REQUEST);

        // then
        // exception expected
    }

    @Test
    public void shouldStoreLog() throws LoggingEventProcessingFailureException {

        // when
        ResponseEntity<Void> result = logsController.storeLog(LOGGING_EVENT);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(result.getBody(), nullValue());
        verify(logProcessingService).storeLog(LOGGING_EVENT);
    }

    @Test(expected = LoggingEventProcessingFailureException.class)
    public void shouldStoreLogThrowLoggingEventProcessingException() throws LoggingEventProcessingFailureException {

        // given
        doThrow(RuntimeException.class).when(logProcessingService).storeLog(LOGGING_EVENT);

        // when
        logsController.storeLog(LOGGING_EVENT);

        // then
        // exception expected
    }

    @Test
    public void shouldHandleRetrievalException() {

        // given
        LogRetrievalFailureException exception = new LogRetrievalFailureException(LOG_REQUEST, new RuntimeException());

        // when
        ResponseEntity<Map<String, String>> result = logsController.retrievalFailureHandler(exception);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(result.getBody().get(MESSAGE_KEY), equalTo(LOG_RETRIEVAL_FAILURE_MESSAGE));
    }

    @Test
    public void shouldHandleProcessingException() {

        // given
        LoggingEventProcessingFailureException exception = new LoggingEventProcessingFailureException(new RuntimeException());

        // when
        ResponseEntity<Map<String, String>> result = logsController.eventProcessingFailureHandler(exception);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getStatusCode(), equalTo(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(result.getBody().get(MESSAGE_KEY), equalTo(LOGGING_EVENT_PROCESSING_FAILURE_MESSAGE));
    }

    @Test
    public void shouldHandleAnyOtherExceptions() {

        // given
        RuntimeException exception = new RuntimeException("exception occurred");

        // when
        ResponseEntity<Map<String, String>> result = logsController.defaultExceptionHandler(exception);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(result.getBody().get(MESSAGE_KEY), equalTo(UNEXPECTED_EXCEPTION_MESSAGE));
    }
}
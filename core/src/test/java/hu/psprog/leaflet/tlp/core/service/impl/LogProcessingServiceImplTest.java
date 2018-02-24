package hu.psprog.leaflet.tlp.core.service.impl;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.core.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.persistence.dao.LogEventDAO;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder.ExpressionBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link LogProcessingServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class LogProcessingServiceImplTest {

    private static final LoggingEvent LOGGING_EVENT = LoggingEvent.getBuilder().build();
    private static final LogRequest LOG_REQUEST = new LogRequest();
    private static final LogEventPage LOG_EVENT_PAGE = LogEventPage.getBuilder().build();

    @Mock
    private LogEventDAO logEventDAO;

    @Mock
    private ConversionService conversionService;

    @Mock
    private ExpressionBuilder expressionBuilder;

    @Mock
    private Pageable pageable;

    @Mock
    private Predicate predicate;

    @Mock
    private Page<LoggingEvent> loggingEventPage;

    @InjectMocks
    private LogProcessingServiceImpl logProcessingService;

    @Test
    public void shouldGetPagedLogs() {

        // given
        given(expressionBuilder.build(LOG_REQUEST)).willReturn(Optional.of(predicate));
        given(conversionService.convert(LOG_REQUEST, Pageable.class)).willReturn(pageable);
        given(logEventDAO.findAll(predicate, pageable)).willReturn(loggingEventPage);
        given(conversionService.convert(loggingEventPage, LogEventPage.class)).willReturn(LOG_EVENT_PAGE);

        // when
        LogEventPage result = logProcessingService.getLogs(LOG_REQUEST);

        // then
        assertThat(result, equalTo(LOG_EVENT_PAGE));
    }

    @Test
    public void shouldGetPagedAndFilteredLogs() {

        // given
        given(expressionBuilder.build(LOG_REQUEST)).willReturn(Optional.empty());
        given(conversionService.convert(LOG_REQUEST, Pageable.class)).willReturn(pageable);
        given(logEventDAO.findAll(pageable)).willReturn(loggingEventPage);
        given(conversionService.convert(loggingEventPage, LogEventPage.class)).willReturn(LOG_EVENT_PAGE);

        // when
        LogEventPage result = logProcessingService.getLogs(LOG_REQUEST);

        // then
        assertThat(result, equalTo(LOG_EVENT_PAGE));
    }

    @Test
    public void shouldStoreLog() {

        // when
        logProcessingService.storeLog(LOGGING_EVENT);

        // then
        verify(logEventDAO).save(LOGGING_EVENT);
    }
}
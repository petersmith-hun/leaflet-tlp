package hu.psprog.leaflet.tlp.core.service.impl;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.persistence.dao.LogEventDAO;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder.ExpressionBuilder;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import hu.psprog.leaflet.tlql.processor.TLQLProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
public class LogProcessingServiceImplTest {

    private static final LoggingEvent LOGGING_EVENT = LoggingEvent.getBuilder().build();
    private static final DSLQueryModel DSL_QUERY_MODEL = new DSLQueryModel();
    private static final LogRequest LOG_REQUEST = new LogRequest();
    private static final LogEventPage LOG_EVENT_PAGE = LogEventPage.getBuilder().build();
    private static final String TLQL_STRING = "search with conditions source = 'lcfa'";

    @Mock
    private LogEventDAO logEventDAO;

    @Mock
    private ConversionService conversionService;

    @Mock
    private ExpressionBuilder expressionBuilder;

    @Mock
    private TLQLProcessorService tlqlProcessorService;

    @Mock
    private Pageable pageable;

    @Mock
    private Predicate predicate;

    @Mock
    private Page<LoggingEvent> loggingEventPage;

    @InjectMocks
    private LogProcessingServiceImpl logProcessingService;

    @Test
    public void shouldGetPagedAndFilteredLogsForLogRequest() {

        // given
        given(conversionService.convert(LOG_REQUEST, DSLQueryModel.class)).willReturn(DSL_QUERY_MODEL);
        given(expressionBuilder.build(DSL_QUERY_MODEL)).willReturn(Optional.of(predicate));
        given(conversionService.convert(DSL_QUERY_MODEL, Pageable.class)).willReturn(pageable);
        given(logEventDAO.findAll(predicate, pageable)).willReturn(loggingEventPage);
        given(conversionService.convert(loggingEventPage, LogEventPage.class)).willReturn(LOG_EVENT_PAGE);

        // when
        LogEventPage result = logProcessingService.getLogs(LOG_REQUEST);

        // then
        assertThat(result, equalTo(LOG_EVENT_PAGE));
    }

    @Test
    public void shouldGetPagedLogsForLogRequest() {

        // given
        given(conversionService.convert(LOG_REQUEST, DSLQueryModel.class)).willReturn(DSL_QUERY_MODEL);
        given(expressionBuilder.build(DSL_QUERY_MODEL)).willReturn(Optional.empty());
        given(conversionService.convert(DSL_QUERY_MODEL, Pageable.class)).willReturn(pageable);
        given(logEventDAO.findAll(pageable)).willReturn(loggingEventPage);
        given(conversionService.convert(loggingEventPage, LogEventPage.class)).willReturn(LOG_EVENT_PAGE);

        // when
        LogEventPage result = logProcessingService.getLogs(LOG_REQUEST);

        // then
        assertThat(result, equalTo(LOG_EVENT_PAGE));
    }

    @Test
    public void shouldGetPagedAndFilteredLogsForTLQLString() {

        // given
        given(tlqlProcessorService.parse(TLQL_STRING)).willReturn(DSL_QUERY_MODEL);
        given(expressionBuilder.build(DSL_QUERY_MODEL)).willReturn(Optional.of(predicate));
        given(conversionService.convert(DSL_QUERY_MODEL, Pageable.class)).willReturn(pageable);
        given(logEventDAO.findAll(predicate, pageable)).willReturn(loggingEventPage);
        given(conversionService.convert(loggingEventPage, LogEventPage.class)).willReturn(LOG_EVENT_PAGE);

        // when
        LogEventPage result = logProcessingService.getLogs(TLQL_STRING);

        // then
        assertThat(result, equalTo(LOG_EVENT_PAGE));
    }

    @Test
    public void shouldGetPagedLogsForTLQLString() {

        // given
        given(tlqlProcessorService.parse(TLQL_STRING)).willReturn(DSL_QUERY_MODEL);
        given(expressionBuilder.build(DSL_QUERY_MODEL)).willReturn(Optional.empty());
        given(conversionService.convert(DSL_QUERY_MODEL, Pageable.class)).willReturn(pageable);
        given(logEventDAO.findAll(pageable)).willReturn(loggingEventPage);
        given(conversionService.convert(loggingEventPage, LogEventPage.class)).willReturn(LOG_EVENT_PAGE);

        // when
        LogEventPage result = logProcessingService.getLogs(TLQL_STRING);

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
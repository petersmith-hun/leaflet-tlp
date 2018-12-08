package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link PageLoggingEventToLogEventPageConverter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class PageLoggingEventToLogEventPageConverterTest {

    private static final LoggingEvent CORE_LOGGING_EVENT_1 = LoggingEvent.getBuilder().withContent("log1").build();
    private static final LoggingEvent CORE_LOGGING_EVENT_2 = LoggingEvent.getBuilder().withContent("log2").build();
    private static final hu.psprog.leaflet.tlp.api.domain.LoggingEvent API_LOGGING_EVENT_1 = hu.psprog.leaflet.tlp.api.domain.LoggingEvent.getBuilder().withContent("log1").build();
    private static final hu.psprog.leaflet.tlp.api.domain.LoggingEvent API_LOGGING_EVENT_2 = hu.psprog.leaflet.tlp.api.domain.LoggingEvent.getBuilder().withContent("log2").build();

    @Mock
    private LoggingEventEntityToDomainConverter entityConverter;

    @InjectMocks
    private PageLoggingEventToLogEventPageConverter converter;

    @Test
    public void shouldConvert() {

        // given
        List<LoggingEvent> loggingEventList = Arrays.asList(CORE_LOGGING_EVENT_1, CORE_LOGGING_EVENT_2);
        List<hu.psprog.leaflet.tlp.api.domain.LoggingEvent> expectedEventList = Arrays.asList(API_LOGGING_EVENT_1, API_LOGGING_EVENT_2);
        Page<LoggingEvent> source = new PageImpl<>(loggingEventList, PageRequest.of(1, 2), 5);
        given(entityConverter.convert(CORE_LOGGING_EVENT_1)).willReturn(API_LOGGING_EVENT_1);
        given(entityConverter.convert(CORE_LOGGING_EVENT_2)).willReturn(API_LOGGING_EVENT_2);

        // when
        LogEventPage result = converter.convert(source);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getEntitiesOnPage(), equalTo(expectedEventList));
        assertThat(result.getPageCount(), equalTo(3));
        assertThat(result.getPageSize(), equalTo(2));
        assertThat(result.getPageNumber(), equalTo(2));
        assertThat(result.getEntityCount(), equalTo(5L));
        assertThat(result.getEntityCountOnPage(), equalTo(2));
        assertThat(result.isFirst(), is(false));
        assertThat(result.isLast(), is(false));
        assertThat(result.isHasNext(), is(true));
        assertThat(result.isHasPrevious(), is(true));
    }
}
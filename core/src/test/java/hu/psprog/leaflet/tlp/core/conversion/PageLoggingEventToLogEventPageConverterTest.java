package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link PageLoggingEventToLogEventPageConverter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class PageLoggingEventToLogEventPageConverterTest {

    @InjectMocks
    private PageLoggingEventToLogEventPageConverter converter;

    @Test
    public void shouldConvert() {

        // given
        List<LoggingEvent> loggingEventList = Arrays.asList(
                LoggingEvent.getBuilder().withContent("log1").build(),
                LoggingEvent.getBuilder().withContent("log2").build());
        Page<LoggingEvent> source = new PageImpl<>(loggingEventList, new PageRequest(1, 2), 5);

        // when
        LogEventPage result = converter.convert(source);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getEntitiesOnPage(), equalTo(loggingEventList));
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
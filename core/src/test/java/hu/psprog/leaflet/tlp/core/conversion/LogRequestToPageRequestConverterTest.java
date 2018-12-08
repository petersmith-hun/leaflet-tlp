package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.api.domain.OrderBy;
import hu.psprog.leaflet.tlp.api.domain.OrderDirection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link LogRequestToPageRequestConverter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class LogRequestToPageRequestConverterTest {

    @InjectMocks
    private LogRequestToPageRequestConverter converter;

    @Test
    public void shouldConvert() {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setPage(2);
        logRequest.setOrderDirection(OrderDirection.ASC);
        logRequest.setOrderBy(OrderBy.TIMESTAMP);

        // when
        Pageable result = converter.convert(logRequest);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getPageNumber(), equalTo(1));
        assertThat(result.getSort().getOrderFor("timeStamp").getDirection(), equalTo(Sort.Direction.ASC));
    }
}
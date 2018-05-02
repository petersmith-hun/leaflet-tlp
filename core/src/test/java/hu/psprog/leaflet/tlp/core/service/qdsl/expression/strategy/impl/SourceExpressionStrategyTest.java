package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link SourceExpressionStrategy}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class SourceExpressionStrategyTest {

    private static final QLoggingEvent Q_LOGGING_EVENT = new QLoggingEvent("event");
    private static final String SOURCE = "test-app";

    @InjectMocks
    private SourceExpressionStrategy strategy;

    @Test
    public void shouldApplyStrategy() {

        // given
        LogRequest logRequest = prepareLogRequest(SOURCE);
        Predicate expectedExpression = new BooleanBuilder()
                .and(Q_LOGGING_EVENT.source.equalsIgnoreCase(SOURCE))
                .getValue();

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(expectedExpression));
    }

    @Test
    public void shouldApplyStrategyReturnEmptyOptionalForEmptySource() {

        // given
        LogRequest logRequest = prepareLogRequest(StringUtils.EMPTY);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void shouldApplyStrategyReturnEmptyOptionalForNullSource() {

        // given
        LogRequest logRequest = prepareLogRequest(null);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    private LogRequest prepareLogRequest(String source) {

        LogRequest logRequest = new LogRequest();
        logRequest.setSource(source);

        return logRequest;
    }
}
package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link ContentExpressionStrategy}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ContentExpressionStrategyTest {

    private static final QLoggingEvent Q_LOGGING_EVENT = new QLoggingEvent("event");
    private static final String TEST_CONTENT = "test-content";

    @InjectMocks
    private ContentExpressionStrategy strategy;

    @Test
    public void shouldApplyStrategy() {

        // given
        LogRequest logRequest = prepareLogRequest(TEST_CONTENT);
        Predicate expectedExpression = new BooleanBuilder()
                .or(Q_LOGGING_EVENT.content.containsIgnoreCase(TEST_CONTENT))
                .or(Q_LOGGING_EVENT.exception.message.containsIgnoreCase(TEST_CONTENT))
                .or(Q_LOGGING_EVENT.exception.stackTrace.containsIgnoreCase(TEST_CONTENT))
                .getValue();

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(expectedExpression));
    }

    @Test
    public void shouldApplyStrategyReturnEmptyOptionalForNullContent() {

        // given
        LogRequest logRequest = prepareLogRequest(null);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void shouldApplyStrategyReturnEmptyOptionalForEmptyContent() {

        // given
        LogRequest logRequest = prepareLogRequest(StringUtils.EMPTY);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    private LogRequest prepareLogRequest(String content) {

        LogRequest logRequest = new LogRequest();
        logRequest.setContent(content);

        return logRequest;
    }
}
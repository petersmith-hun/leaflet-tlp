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
 * Unit tests for {@link LevelExpressionStrategy}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class LevelExpressionStrategyTest {

    private static final QLoggingEvent Q_LOGGING_EVENT = new QLoggingEvent("event");
    private static final String LEVEL = "WARN";

    @InjectMocks
    private LevelExpressionStrategy strategy;

    @Test
    public void shouldApplyStrategy() {

        // given
        LogRequest logRequest = prepareLogRequest(LEVEL);
        Predicate expectedExpression = new BooleanBuilder()
                .and(Q_LOGGING_EVENT.level.equalsIgnoreCase(LEVEL))
                .getValue();

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(expectedExpression));
    }

    @Test
    public void shouldApplyStrategyReturnEmptyOptionalForEmptyLevel() {

        // given
        LogRequest logRequest = prepareLogRequest(StringUtils.EMPTY);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void shouldApplyStrategyReturnEmptyOptionalForNullLevel() {

        // given
        LogRequest logRequest = prepareLogRequest(null);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    private LogRequest prepareLogRequest(String level) {

        LogRequest logRequest = new LogRequest();
        logRequest.setLevel(level);

        return logRequest;
    }
}
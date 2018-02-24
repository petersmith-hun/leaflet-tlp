package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link TimestampExpressionStrategy}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TimestampExpressionStrategyTest {

    private static final QLoggingEvent Q_LOGGING_EVENT = new QLoggingEvent("event");
    private static final Date DATE_FROM = prepareDate(-3);
    private static final Date DATE_TO = prepareDate(-1);

    @InjectMocks
    private TimestampExpressionStrategy strategy;

    @Test
    public void shouldApplyStrategyWithBetweenExpression() {

        // given
        LogRequest logRequest = prepareLogRequest(true, true);
        Predicate expectedExpression = new BooleanBuilder()
                .and(Q_LOGGING_EVENT.timeStamp.between(DATE_FROM, DATE_TO))
                .getValue();

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertAppliedStrategy(result, expectedExpression);
    }

    @Test
    public void shouldApplyStrategyWithAfterExpression() {

        // given
        LogRequest logRequest = prepareLogRequest(true, false);
        Predicate expectedExpression = new BooleanBuilder()
                .and(Q_LOGGING_EVENT.timeStamp.after(DATE_FROM))
                .getValue();

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertAppliedStrategy(result, expectedExpression);
    }

    @Test
    public void shouldApplyStrategyWithBeforeExpression() {

        // given
        LogRequest logRequest = prepareLogRequest(false, true);
        Predicate expectedExpression = new BooleanBuilder()
                .and(Q_LOGGING_EVENT.timeStamp.before(DATE_TO))
                .getValue();

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertAppliedStrategy(result, expectedExpression);
    }

    @Test
    public void shouldApplyStrategyReturnWithEmptyOptional() {

        // given
        LogRequest logRequest = prepareLogRequest(false, false);

        // when
        Optional<BooleanExpression> result = strategy.applyStrategy(Q_LOGGING_EVENT, logRequest);

        // then
        assertThat(result.isPresent(), is(false));
    }

    private void assertAppliedStrategy(Optional<BooleanExpression> result, Predicate expectedExpression) {
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(expectedExpression));
    }

    private LogRequest prepareLogRequest(boolean withFrom, boolean withTo) {

        LogRequest logRequest = new LogRequest();
        if (withFrom) {
            logRequest.setFrom(DATE_FROM);
        }
        if (withTo) {
            logRequest.setTo(DATE_TO);
        }

        return logRequest;
    }

    private static Date prepareDate(int dayOffset) {

        Calendar calendar = new Calendar.Builder()
                .setInstant(new Date())
                .build();
        calendar.add(Calendar.DAY_OF_MONTH, dayOffset);

        return calendar.getTime();
    }
}
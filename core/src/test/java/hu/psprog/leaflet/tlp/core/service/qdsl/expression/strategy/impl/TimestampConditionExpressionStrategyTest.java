package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLObjectContext;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import hu.psprog.leaflet.tlql.ir.DSLTimestampValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link TimestampConditionExpressionStrategy}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class TimestampConditionExpressionStrategyTest {

    @Mock
    private MappingRegistry<DateTimePath<Date>, Date> mappingRegistry;

    @InjectMocks
    private TimestampConditionExpressionStrategy timestampConditionExpressionStrategy;

    @Test
    public void shouldApplyStrategyCreateSimpleGreaterThanTimestampExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition("2021-04-15", DSLOperator.GREATER_THAN);

        given(mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP)).willReturn(loggingEvent -> loggingEvent.timeStamp);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.GREATER_THAN)).willReturn(DateTimePath::gt);

        // when
        BooleanExpression result = timestampConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.timeStamp > 2021-04-15 00:00:00.0"));
    }

    @Test
    public void shouldApplyStrategyCreateSimpleNotEqualTimestampExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition("2021-04-20", DSLOperator.NOT_EQUALS);

        given(mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP)).willReturn(loggingEvent -> loggingEvent.timeStamp);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.NOT_EQUALS)).willReturn(DateTimePath::ne);

        // when
        BooleanExpression result = timestampConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.timeStamp != 2021-04-20 00:00:00.0"));
    }

    @Test
    public void shouldApplyStrategyCreateFullInclusiveIntervalTimestampExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition("2021-04-10", "2021-04-13", DSLTimestampValue.IntervalType.FULL_INCLUSIVE);

        given(mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP)).willReturn(loggingEvent -> loggingEvent.timeStamp);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.GREATER_THAN_OR_EQUAL)).willReturn(DateTimePath::goe);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.LESS_THAN_OR_EQUAL)).willReturn(DateTimePath::loe);

        // when
        BooleanExpression result = timestampConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.timeStamp >= 2021-04-10 00:00:00.0 && loggingEvent.timeStamp <= 2021-04-13 00:00:00.0"));
    }

    @Test
    public void shouldApplyStrategyCreateFullExclusiveIntervalTimestampExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition("2021-04-11", "2021-04-14", DSLTimestampValue.IntervalType.FULL_EXCLUSIVE);

        given(mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP)).willReturn(loggingEvent -> loggingEvent.timeStamp);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.GREATER_THAN)).willReturn(DateTimePath::gt);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.LESS_THAN)).willReturn(DateTimePath::lt);

        // when
        BooleanExpression result = timestampConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.timeStamp > 2021-04-11 00:00:00.0 && loggingEvent.timeStamp < 2021-04-14 00:00:00.0"));
    }

    @Test
    public void shouldApplyStrategyCreateInclusiveToExclusiveIntervalTimestampExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition("2021-04-09", "2021-04-12", DSLTimestampValue.IntervalType.INCLUSIVE_TO_EXCLUSIVE);

        given(mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP)).willReturn(loggingEvent -> loggingEvent.timeStamp);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.GREATER_THAN_OR_EQUAL)).willReturn(DateTimePath::goe);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.LESS_THAN)).willReturn(DateTimePath::lt);

        // when
        BooleanExpression result = timestampConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.timeStamp >= 2021-04-09 00:00:00.0 && loggingEvent.timeStamp < 2021-04-12 00:00:00.0"));
    }

    @Test
    public void shouldApplyStrategyCreateExclusiveToInclusiveIntervalTimestampExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition("2021-04-07", "2021-04-09", DSLTimestampValue.IntervalType.EXCLUSIVE_TO_INCLUSIVE);

        given(mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP)).willReturn(loggingEvent -> loggingEvent.timeStamp);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.GREATER_THAN)).willReturn(DateTimePath::gt);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.LESS_THAN_OR_EQUAL)).willReturn(DateTimePath::loe);

        // when
        BooleanExpression result = timestampConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.timeStamp > 2021-04-07 00:00:00.0 && loggingEvent.timeStamp <= 2021-04-09 00:00:00.0"));
    }

    @Test
    public void shouldForGroupReturnTimestampConditionExpressionStrategyGroup() {

        // when
        ExpressionStrategyGroup result = timestampConditionExpressionStrategy.forGroup();

        // then
        assertThat(result, equalTo(ExpressionStrategyGroup.TIMESTAMP_CONDITION));
    }

    private DSLCondition prepareDSLCondition(String simpleDate, DSLOperator dslOperator) {
        return prepareDSLCondition(simpleDate, null, dslOperator, DSLTimestampValue.IntervalType.NONE);
    }

    private DSLCondition prepareDSLCondition(String leftDate, String rightDate, DSLTimestampValue.IntervalType intervalType) {
        return prepareDSLCondition(leftDate, rightDate, DSLOperator.BETWEEN, intervalType);
    }

    private DSLCondition prepareDSLCondition(String leftDate, String rightDate, DSLOperator dslOperator, DSLTimestampValue.IntervalType intervalType) {

        DSLCondition dslCondition = new DSLCondition();
        dslCondition.setObjectContext(new DSLObjectContext(DSLObject.TIMESTAMP, null));
        dslCondition.setOperator(dslOperator);
        dslCondition.setTimestampValue(intervalType == DSLTimestampValue.IntervalType.NONE
                ? new DSLTimestampValue(prepareLocalDateTime(leftDate))
                : new DSLTimestampValue(intervalType, prepareLocalDateTime(leftDate), prepareLocalDateTime(rightDate)));

        return dslCondition;
    }

    private LocalDateTime prepareLocalDateTime(String dateTimeString) {
        return LocalDateTime.of(LocalDate.parse(dateTimeString), LocalTime.MIDNIGHT);
    }
}

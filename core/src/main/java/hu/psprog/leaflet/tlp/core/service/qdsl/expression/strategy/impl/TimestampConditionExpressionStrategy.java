package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.SimpleExpression;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import hu.psprog.leaflet.tlql.ir.DSLTimestampValue;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * {@link ExpressionStrategy} implementation for timestamp expressions.
 * Strategy supports only the "timestamp" field, and it can apply the following operators:
 *  - equals,
 *  - not equals,
 *  - greater than (or equal to),
 *  - less than (or equal to),
 *  - between.
 *
 * @author Peter Smith
 */
@Component
public class TimestampConditionExpressionStrategy implements ExpressionStrategy {

    private static final Map<DSLOperator, BiFunction<DateTimePath<Date>, Date, BooleanExpression>> BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EQUALS, SimpleExpression::eq,
            DSLOperator.NOT_EQUALS, SimpleExpression::ne,
            DSLOperator.GREATER_THAN, DateTimePath::gt,
            DSLOperator.GREATER_THAN_OR_EQUAL, DateTimePath::goe,
            DSLOperator.LESS_THAN, DateTimePath::lt,
            DSLOperator.LESS_THAN_OR_EQUAL, DateTimePath::loe
    );

    private static final Map<DSLTimestampValue.IntervalType, Pair<DSLOperator, DSLOperator>> INTERVAL_BOOLEAN_EXPRESSION_MAP = Map.of(
            DSLTimestampValue.IntervalType.FULL_EXCLUSIVE, Pair.of(DSLOperator.GREATER_THAN, DSLOperator.LESS_THAN),
            DSLTimestampValue.IntervalType.FULL_INCLUSIVE, Pair.of(DSLOperator.GREATER_THAN_OR_EQUAL, DSLOperator.LESS_THAN_OR_EQUAL),
            DSLTimestampValue.IntervalType.EXCLUSIVE_TO_INCLUSIVE, Pair.of(DSLOperator.GREATER_THAN, DSLOperator.LESS_THAN_OR_EQUAL),
            DSLTimestampValue.IntervalType.INCLUSIVE_TO_EXCLUSIVE, Pair.of(DSLOperator.GREATER_THAN_OR_EQUAL, DSLOperator.LESS_THAN)
    );

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        BooleanExpression expression;
        if (dslCondition.getOperator() == DSLOperator.BETWEEN) {

            DSLTimestampValue timestampValue = dslCondition.getTimestampValue();
            Pair<DSLOperator, DSLOperator> intervalOperatorPair = INTERVAL_BOOLEAN_EXPRESSION_MAP.get(timestampValue.getIntervalType());
            BooleanExpression leftExpression = createIntervalExpression(event, intervalOperatorPair.getLeft(), timestampValue.getLeftOrSimple());
            BooleanExpression rightExpression = createIntervalExpression(event, intervalOperatorPair.getRight(), timestampValue.getRight());

            expression = leftExpression.and(rightExpression);
        } else {
            expression = BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslCondition.getOperator())
                    .apply(event.timeStamp, Timestamp.valueOf(dslCondition.getTimestampValue().getLeftOrSimple()));
        }

        return expression;
    }

    @Override
    public ExpressionStrategyGroup forGroup() {
        return ExpressionStrategyGroup.TIMESTAMP_CONDITION;
    }

    private BooleanExpression createIntervalExpression(QLoggingEvent event, DSLOperator dslOperator, LocalDateTime value) {

        return BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslOperator).apply(event.timeStamp, Timestamp.valueOf(value));
    }
}

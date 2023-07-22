package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import hu.psprog.leaflet.tlql.ir.DSLTimestampValue;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

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

    private static final Map<DSLTimestampValue.IntervalType, Pair<DSLOperator, DSLOperator>> INTERVAL_BOOLEAN_EXPRESSION_MAP = Map.of(
            DSLTimestampValue.IntervalType.FULL_EXCLUSIVE, Pair.of(DSLOperator.GREATER_THAN, DSLOperator.LESS_THAN),
            DSLTimestampValue.IntervalType.FULL_INCLUSIVE, Pair.of(DSLOperator.GREATER_THAN_OR_EQUAL, DSLOperator.LESS_THAN_OR_EQUAL),
            DSLTimestampValue.IntervalType.EXCLUSIVE_TO_INCLUSIVE, Pair.of(DSLOperator.GREATER_THAN, DSLOperator.LESS_THAN_OR_EQUAL),
            DSLTimestampValue.IntervalType.INCLUSIVE_TO_EXCLUSIVE, Pair.of(DSLOperator.GREATER_THAN_OR_EQUAL, DSLOperator.LESS_THAN)
    );

    private final MappingRegistry<DateTimePath<Date>, Date> mappingRegistry;

    @Autowired
    public TimestampConditionExpressionStrategy(MappingRegistry<DateTimePath<Date>, Date> mappingRegistry) {
        this.mappingRegistry = mappingRegistry;
    }

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        DSLTimestampValue timestampValue = dslCondition.getTimestampValue();
        DSLOperator operator = dslCondition.getOperator();
        DateTimePath<Date> path = mappingRegistry.getQDSLPath(dslCondition.getObjectContext().getObject()).apply(event);

        return operator == DSLOperator.BETWEEN
                ? createIntervalExpression(path, timestampValue)
                : createExpression(path, operator, timestampValue.getLeftOrSimple());
    }

    @Override
    public ExpressionStrategyGroup forGroup() {
        return ExpressionStrategyGroup.TIMESTAMP_CONDITION;
    }

    private BooleanExpression createIntervalExpression(DateTimePath<Date> path, DSLTimestampValue timestampValue) {

        Pair<DSLOperator, DSLOperator> intervalOperatorPair = getIntervalOperatorPair(timestampValue);
        BooleanExpression leftExpression = createExpression(path, intervalOperatorPair.getLeft(), timestampValue.getLeftOrSimple());
        BooleanExpression rightExpression = createExpression(path, intervalOperatorPair.getRight(), timestampValue.getRight());

        return leftExpression.and(rightExpression);
    }

    private BooleanExpression createExpression(DateTimePath<Date> path, DSLOperator dslOperator, LocalDateTime value) {
        return mappingRegistry.getSingleValueExpressionGenerator(dslOperator).apply(path, Timestamp.valueOf(value));
    }

    private static Pair<DSLOperator, DSLOperator> getIntervalOperatorPair(DSLTimestampValue timestampValue) {
        return INTERVAL_BOOLEAN_EXPRESSION_MAP.get(timestampValue.getIntervalType());
    }
}

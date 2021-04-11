package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link ExpressionStrategy} implementation for "flexible-equality" based expressions.
 * Supports the "logger" and "message" fields, with the following operators:
 *  - equals,
 *  - not equals,
 *  - like.
 *
 * @author Peter Smith
 */
@Component
public class GenericFlexibleConditionExpressionStrategy implements ExpressionStrategy {

    private static final Map<DSLObject, Function<QLoggingEvent, StringPath>> OBJECT_TO_QDSL_PATH_MAP = Map.of(
            DSLObject.LOGGER, event -> event.loggerName,
            DSLObject.MESSAGE, event -> event.content
    );

    private static final Map<DSLOperator, BiFunction<StringPath, String, BooleanExpression>> BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EQUALS, StringExpression::equalsIgnoreCase,
            DSLOperator.NOT_EQUALS, StringExpression::notEqualsIgnoreCase,
            DSLOperator.LIKE, StringExpression::containsIgnoreCase
    );

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        StringPath path = OBJECT_TO_QDSL_PATH_MAP.get(dslCondition.getObject()).apply(event);

        return BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslCondition.getOperator()).apply(path, dslCondition.getValue());
    }

    @Override
    public ExpressionStrategyGroup forGroup() {
        return ExpressionStrategyGroup.GENERIC_FLEXIBLE_CONDITION;
    }
}

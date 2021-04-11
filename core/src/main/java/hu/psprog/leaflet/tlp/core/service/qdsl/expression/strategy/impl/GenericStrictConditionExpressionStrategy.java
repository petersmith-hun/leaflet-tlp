package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
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
 * {@link ExpressionStrategy} implementation for "strict-equality" based expressions.
 * Supports the "source" and "level" fields, with the following operators:
 *  - equals,
 *  - not equals,
 *  - either,
 *  - none.
 *
 * TODO this could be generalized in theory:
 *      one single generic strategy would be enough (with extracted mapping)
 *      for timestamps another is obviously needed, but that's all
 *
 * @author Peter Smith
 */
@Component
public class GenericStrictConditionExpressionStrategy implements ExpressionStrategy {

    private static final Map<DSLObject, Function<QLoggingEvent, StringPath>> OBJECT_TO_QDSL_PATH_MAP = Map.of(
            DSLObject.SOURCE, event -> event.source,
            DSLObject.LEVEL, event -> event.level
    );

    private static final Map<DSLOperator, BiFunction<StringPath, String, BooleanExpression>> SINGLE_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EQUALS, StringExpression::equalsIgnoreCase,
            DSLOperator.NOT_EQUALS, StringExpression::notEqualsIgnoreCase
    );

    private static final Map<DSLOperator, BiFunction<StringPath, String[], BooleanExpression>> MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EITHER, SimpleExpression::in,
            DSLOperator.NONE, SimpleExpression::notIn
    );

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        StringPath path = OBJECT_TO_QDSL_PATH_MAP.get(dslCondition.getObject()).apply(event);

        return MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.containsKey(dslCondition.getOperator())
                ? MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslCondition.getOperator()).apply(path, prepareMultiValueExpression(dslCondition))
                : SINGLE_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslCondition.getOperator()).apply(path, dslCondition.getValue());
    }

    private String[] prepareMultiValueExpression(DSLCondition dslCondition) {

        return dslCondition.getMultipleValue().stream()
                .map(value -> dslCondition.getObject() == DSLObject.LEVEL
                        ? value.toUpperCase()
                        : value)
                .toArray(String[]::new);
    }

    @Override
    public ExpressionStrategyGroup forGroup() {
        return ExpressionStrategyGroup.GENERIC_STRICT_CONDITION;
    }
}

package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * {@link ExpressionStrategy} implementation for textual field expressions.
 * Supports the "source", "level", "message" and "logger" fields, with the following operators:
 *  - equals,
 *  - not equals,
 *  - either,
 *  - none,
 *  - like.
 *
 * @author Peter Smith
 */
@Component
public class TextConditionExpressionStrategy implements ExpressionStrategy {

    private static final List<DSLOperator> MULTI_VALUE_EXPRESSION_OPERATORS = Arrays.asList(DSLOperator.EITHER, DSLOperator.NONE);

    private final MappingRegistry<StringPath, String> mappingRegistry;

    @Autowired
    public TextConditionExpressionStrategy(MappingRegistry<StringPath, String> mappingRegistry) {
        this.mappingRegistry = mappingRegistry;
    }

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        StringPath path = mappingRegistry.getQDSLPath(dslCondition.getObject()).apply(event);
        DSLOperator operator = dslCondition.getOperator();

        return isMultiValueExpression(operator)
                ? mappingRegistry.getMultiValueExpressionGenerator(operator).apply(path, prepareMultiValueExpression(dslCondition))
                : mappingRegistry.getSingleValueExpressionGenerator(operator).apply(path, dslCondition.getValue());
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
        return ExpressionStrategyGroup.TEXT_CONDITION;
    }

    private boolean isMultiValueExpression(DSLOperator dslOperator) {
        return MULTI_VALUE_EXPRESSION_OPERATORS.contains(dslOperator);
    }
}

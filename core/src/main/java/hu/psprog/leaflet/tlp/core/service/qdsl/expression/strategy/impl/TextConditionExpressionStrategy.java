package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link AbstractTextConditionExpressionStrategy} implementation for textual field expressions.
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
public class TextConditionExpressionStrategy extends AbstractTextConditionExpressionStrategy {

    private final MappingRegistry<StringPath, String> mappingRegistry;

    @Autowired
    public TextConditionExpressionStrategy(MappingRegistry<StringPath, String> mappingRegistry) {
        this.mappingRegistry = mappingRegistry;
    }

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        StringPath path = mappingRegistry.getQDSLPath(dslCondition.getObjectContext().getObject()).apply(event);
        DSLOperator operator = dslCondition.getOperator();

        return isMultiValueExpression(operator)
                ? mappingRegistry.getMultiValueExpressionGenerator(operator).apply(path, prepareMultiValueExpression(dslCondition))
                : mappingRegistry.getSingleValueExpressionGenerator(operator).apply(path, dslCondition.getValue());
    }

    private String[] prepareMultiValueExpression(DSLCondition dslCondition) {

        return dslCondition.getMultipleValue().stream()
                .map(value -> dslCondition.getObjectContext().getObject() == DSLObject.LEVEL
                        ? value.toUpperCase()
                        : value)
                .toArray(String[]::new);
    }

    @Override
    public ExpressionStrategyGroup forGroup() {
        return ExpressionStrategyGroup.TEXT_CONDITION;
    }
}

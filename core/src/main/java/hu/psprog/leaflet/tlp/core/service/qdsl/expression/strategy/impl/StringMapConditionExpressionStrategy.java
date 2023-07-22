package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * {@link AbstractTextConditionExpressionStrategy} implementation for text-map field expressions.
 * Supports the "context" field, with the following operators:
 *  - equals,
 *  - not equals,
 *  - either,
 *  - none,
 *  - like.
 *
 * @author Peter Smith
 */
@Component
public class StringMapConditionExpressionStrategy extends AbstractTextConditionExpressionStrategy {

    private final MappingRegistry<MapPath<String, String, StringPath>, Map<String, String>> mappingRegistry;

    @Autowired
    public StringMapConditionExpressionStrategy(MappingRegistry<MapPath<String, String, StringPath>, Map<String, String>> mappingRegistry) {
        this.mappingRegistry = mappingRegistry;
    }

    @Override
    public BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition) {

        MapPath<String, String, StringPath> path = mappingRegistry.getQDSLPath(dslCondition.getObjectContext().getObject()).apply(event);
        DSLOperator operator = dslCondition.getOperator();

        return isMultiValueExpression(dslCondition.getOperator())
                ? mappingRegistry.getMultiValueExpressionGenerator(operator).apply(path, createParameterMapArray(dslCondition))
                : mappingRegistry.getSingleValueExpressionGenerator(operator).apply(path, createParameterMap(dslCondition, dslCondition.getValue()));
    }

    @Override
    public ExpressionStrategyGroup forGroup() {
        return ExpressionStrategyGroup.TEXT_MAP_CONDITION;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String>[] createParameterMapArray(DSLCondition dslCondition) {

        return dslCondition.getMultipleValue()
                .stream()
                .map(value -> createParameterMap(dslCondition, value))
                .toArray(Map[]::new);
    }

    private Map<String, String> createParameterMap(DSLCondition dslCondition, String value) {
        return Map.of(dslCondition.getObjectContext().getSpecialization(), value);
    }
}

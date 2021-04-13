package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.SimpleExpression;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link MappingRegistry} implementation for database fields accessible via {@link DateTimePath} QueryDSL expressions.
 * Currently used only for the "timestamp" field of log records, using {@link Date} as a base type.
 *
 * @author Peter Smith
 */
@Component
public class DateQDSLPathMappingRegistry implements MappingRegistry<DateTimePath<Date>, Date> {

    private static final Map<DSLOperator, BiFunction<DateTimePath<Date>, Date, BooleanExpression>> BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EQUALS, SimpleExpression::eq,
            DSLOperator.NOT_EQUALS, SimpleExpression::ne,
            DSLOperator.GREATER_THAN, DateTimePath::gt,
            DSLOperator.GREATER_THAN_OR_EQUAL, DateTimePath::goe,
            DSLOperator.LESS_THAN, DateTimePath::lt,
            DSLOperator.LESS_THAN_OR_EQUAL, DateTimePath::loe
    );

    @Override
    public Function<QLoggingEvent, DateTimePath<Date>> getQDSLPath(DSLObject dslObject) {

        if (DSLObject.TIMESTAMP != dslObject) {
            throw new IllegalArgumentException("DSL object must be TIMESTAMP for timestamp-based conditions.");
        }

        return event -> event.timeStamp;
    }

    @Override
    public BiFunction<DateTimePath<Date>, Date, BooleanExpression> getSingleValueExpressionGenerator(DSLOperator dslOperator) {
        return BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslOperator);
    }

    @Override
    public BiFunction<DateTimePath<Date>, Date[], BooleanExpression> getMultiValueExpressionGenerator(DSLOperator dslOperator) {
        throw new UnsupportedOperationException("Timestamp value processing does not allow multi-value matches");
    }
}

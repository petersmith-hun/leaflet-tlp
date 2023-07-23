package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link MappingRegistry} implementation for database fields accessible via {@link StringPath} QueryDSL expressions.
 * Currently used for source, level, logger, message and thread fields of log records. Base type is (implicitly) string.
 *
 * @author Peter Smith
 */
@Component
public class StringQDSLPathMappingRegistry implements MappingRegistry<StringPath, String> {

    private static final Map<DSLObject, Function<QLoggingEvent, StringPath>> OBJECT_TO_QDSL_PATH_MAP = Map.of(
            DSLObject.SOURCE, event -> event.source,
            DSLObject.LEVEL, event -> event.level,
            DSLObject.LOGGER, event -> event.loggerName,
            DSLObject.MESSAGE, event -> event.content,
            DSLObject.THREAD, event -> event.threadName
    );

    private static final Map<DSLOperator, BiFunction<StringPath, String, BooleanExpression>> SINGLE_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EQUALS, StringExpression::equalsIgnoreCase,
            DSLOperator.NOT_EQUALS, StringExpression::notEqualsIgnoreCase,
            DSLOperator.LIKE, StringExpression::containsIgnoreCase
    );

    private static final Map<DSLOperator, BiFunction<StringPath, String[], BooleanExpression>> MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EITHER, SimpleExpression::in,
            DSLOperator.NONE, SimpleExpression::notIn
    );

    @Override
    public Function<QLoggingEvent, StringPath> getQDSLPath(DSLObject dslObject) {

        if (!OBJECT_TO_QDSL_PATH_MAP.containsKey(dslObject)) {
            throw new IllegalArgumentException("DSL object must be either source, level, logger or message");
        }

        return OBJECT_TO_QDSL_PATH_MAP.get(dslObject);
    }

    @Override
    public BiFunction<StringPath, String, BooleanExpression> getSingleValueExpressionGenerator(DSLOperator dslOperator) {
        return SINGLE_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslOperator);
    }

    @Override
    public BiFunction<StringPath, String[], BooleanExpression> getMultiValueExpressionGenerator(DSLOperator dslOperator) {
        return MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslOperator);
    }
}

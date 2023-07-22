package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link MappingRegistry} implementation for database fields accessible via {@link MapPath} QueryDSL expressions.
 * Currently used for context field of log records. Base type is string-string map.
 *
 * @author Peter Smith
 */
@Component
public class StringMapQDSLPathMappingRegistry implements MappingRegistry<MapPath<String, String, StringPath>, Map<String, String>> {

    private static final Map<DSLOperator, BiFunction<MapPath<String, String, StringPath>, Map<String, String>, BooleanExpression>> SINGLE_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EQUALS, (mapPath, valueMap) -> singleValueBaseExpression(valueMap, mapContains(mapPath)),
            DSLOperator.NOT_EQUALS, (mapPath, valueMap) -> singleValueBaseExpression(valueMap, mapContains(mapPath)).not(),
            DSLOperator.LIKE, (mapPath, valueMap) -> singleValueBaseExpression(valueMap, mapContainsLike(mapPath))
    );

    private static final Map<DSLOperator, BiFunction<MapPath<String, String, StringPath>, Map<String, String>[], BooleanExpression>> MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP = Map.of(
            DSLOperator.EITHER, StringMapQDSLPathMappingRegistry::multiValueBaseExpression,
            DSLOperator.NONE, (mapPath, valueMapArray) -> multiValueBaseExpression(mapPath, valueMapArray).not()
    );

    @Override
    public Function<QLoggingEvent, MapPath<String, String, StringPath>> getQDSLPath(DSLObject dslObject) {

        if (dslObject != DSLObject.CONTEXT) {
            throw new IllegalArgumentException("DSL object must be either source, level, logger or message");
        }

        return event -> event.context;
    }

    @Override
    public BiFunction<MapPath<String, String, StringPath>, Map<String, String>, BooleanExpression> getSingleValueExpressionGenerator(DSLOperator dslOperator) {
        return SINGLE_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslOperator);
    }

    @Override
    public BiFunction<MapPath<String, String, StringPath>, Map<String, String>[], BooleanExpression> getMultiValueExpressionGenerator(DSLOperator dslOperator) {
        return MULTI_VALUE_BOOLEAN_EXPRESSION_GENERATOR_MAP.get(dslOperator);
    }

    private static BooleanExpression singleValueBaseExpression(Map<String, String> valueMap, Function<Map.Entry<String, String>, BooleanExpression> booleanExpressionFunction) {

        return valueMap.entrySet()
                .stream()
                // this is always going to be a single item
                .findFirst()
                .map(booleanExpressionFunction)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Key-value pair is missing in source value map: %s", valueMap)));
    }

    private static BooleanExpression multiValueBaseExpression(MapPath<String, String, StringPath> mapPath,
                                                              Map<String, String>[] valueMapArray) {

        return Stream.of(valueMapArray)
                .map(valueMap -> singleValueBaseExpression(valueMap, mapContains(mapPath)))
                .reduce(BooleanExpression::or)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Key-value pair is missing in source value map array: %s", Arrays.toString(valueMapArray))));
    }

    private static Function<Map.Entry<String, String>, BooleanExpression> mapContains(MapPath<String, String, StringPath> mapPath) {
        return entry -> mapPath.contains(entry.getKey(), entry.getValue());
    }

    private static Function<Map.Entry<String, String>, BooleanExpression> mapContainsLike(MapPath<String, String, StringPath> mapPath) {
        return entry -> mapPath.get(entry.getKey()).containsIgnoreCase(entry.getValue());
    }
}

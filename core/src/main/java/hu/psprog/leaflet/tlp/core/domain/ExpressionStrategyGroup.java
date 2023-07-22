package hu.psprog.leaflet.tlp.core.domain;

import hu.psprog.leaflet.tlql.ir.DSLObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Available expression strategy group types with the corresponding processable fields.
 *
 * @author Peter Smith
 */
public enum ExpressionStrategyGroup {

    /**
     * Group for textual value processing.
     */
    TEXT_CONDITION(DSLObject.LEVEL, DSLObject.SOURCE, DSLObject.LOGGER, DSLObject.MESSAGE),

    /**
     * Group for log context value processing.
     */
    TEXT_MAP_CONDITION(DSLObject.CONTEXT),

    /**
     * Group for timestamp value processing.
     */
    TIMESTAMP_CONDITION(DSLObject.TIMESTAMP);

    private static final Map<Object, Object> DSL_OBJECT_EXPRESSION_STRATEGY_MAP = Stream.of(values())
            .flatMap(expressionStrategyGroup -> expressionStrategyGroup.applicableDSLObjects.stream()
                    .map(dslObject -> Pair.of(dslObject, expressionStrategyGroup)))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

    private final List<DSLObject> applicableDSLObjects;

    ExpressionStrategyGroup(DSLObject... applicableDSLObjects) {
        this.applicableDSLObjects = Arrays.asList(applicableDSLObjects);
    }

    /**
     * Returns the corresponding group enum for the provided {@link DSLObject}.
     *
     * @param dslObject {@link DSLObject} enum to retrieve group for
     * @return the corresponding group enum
     */
    public static ExpressionStrategyGroup getByApplicationDSLObject(DSLObject dslObject) {
        return (ExpressionStrategyGroup) DSL_OBJECT_EXPRESSION_STRATEGY_MAP.get(dslObject);
    }
}

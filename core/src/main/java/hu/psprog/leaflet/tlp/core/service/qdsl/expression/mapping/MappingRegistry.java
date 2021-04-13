package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Intermediate representation (DSL objects and operators) mapping function registry to QueryDSL expressions.
 *
 * @author Peter Smith
 */
public interface MappingRegistry<P extends Path<T>, T> {

    /**
     * Returns a mapper function for the given {@link DSLObject} that is able to map
     * a {@link QLoggingEvent} to a QueryDSL {@link Path} field descriptor object.
     *
     * @param dslObject {@link DSLObject} object to be mapped
     * @return mapper function as described above
     */
    Function<QLoggingEvent, P> getQDSLPath(DSLObject dslObject);

    /**
     * Returns a mapper function for the given {@link DSLOperator} that is able to generate
     * a QueryDSL {@link BooleanExpression} based on the given {@link Path} and base date type T.
     * Can be used for single-value matching operators (equal, not equal, greater than, etc.).
     *
     * @param dslOperator {@link DSLOperator} object to be mapped
     * @return mapper function as described above
     */
    BiFunction<P, T, BooleanExpression> getSingleValueExpressionGenerator(DSLOperator dslOperator);

    /**
     * Returns a mapper function for the given {@link DSLOperator} that is able to generate
     * a QueryDSL {@link BooleanExpression} based on the given {@link Path} and an array of base date type T.
     * Can be used for multi-value matching operators (either, none).
     *
     * @param dslOperator {@link DSLOperator} object to be mapped
     * @return mapper function as described above
     */
    BiFunction<P, T[], BooleanExpression> getMultiValueExpressionGenerator(DSLOperator dslOperator);
}

package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import hu.psprog.leaflet.tlql.ir.DSLOperator;

import java.util.Arrays;
import java.util.List;

/**
 * Common abstract {@link ExpressionStrategy} implementation for text or text-like condition expressions.
 *
 * @author Peter Smith
 */
abstract class AbstractTextConditionExpressionStrategy implements ExpressionStrategy {

    /**
     * {@link DSLOperator}s that are supposed to be used in a multi-value condition expression.
     */
    private static final List<DSLOperator> MULTI_VALUE_EXPRESSION_OPERATORS = Arrays.asList(DSLOperator.EITHER, DSLOperator.NONE);

    /**
     * Checks if the given {@link DSLOperator} is a multi-value expression operator.
     *
     * @param dslOperator {@link DSLOperator} to be checked
     * @return {@code true} if the operator is multi-value, {@code false} otherwise
     */
    protected boolean isMultiValueExpression(DSLOperator dslOperator) {
        return MULTI_VALUE_EXPRESSION_OPERATORS.contains(dslOperator);
    }
}

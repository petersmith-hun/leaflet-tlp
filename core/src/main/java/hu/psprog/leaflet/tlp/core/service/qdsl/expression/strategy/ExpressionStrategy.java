package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy;

import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlql.ir.DSLCondition;

/**
 * Expression strategy implementations handle filtering by {@link DSLCondition}.
 * A strategy should be implemented for every {@link ExpressionStrategyGroup}.
 *
 * @author Peter Smith
 */
public interface ExpressionStrategy {

    /**
     * Applies the strategy for given the {@link DSLCondition}.
     * Applying a strategy generates a {@link BooleanExpression} that can be used as a QueryDSL Predicate in a filter chain.
     *
     * @param event meta data instance for {@link LoggingEvent}
     * @param dslCondition {@link DSLCondition} object to be transformed into a {@link BooleanExpression}
     * @return created {@link BooleanExpression} as Optional, or empty Optional if not applicable
     */
    BooleanExpression applyStrategy(QLoggingEvent event, DSLCondition dslCondition);

    /**
     * Returns which {@link ExpressionStrategyGroup} this strategy implementation is linked to.
     *
     * @return corresponding {@link ExpressionStrategyGroup}
     */
    ExpressionStrategyGroup forGroup();
}

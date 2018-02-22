package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy;

import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;

import java.util.Optional;

/**
 * Expression strategy implementations handle filtering by {@link LogRequest}.
 * For every {@link LogRequest} fitler field a strategy should be implemented.
 *
 * @author Peter Smith
 */
public interface ExpressionStrategy {

    /**
     * Tries to apply a strategy for given {@link LogRequest}.
     * If strategy can be applied, the created {@link BooleanExpression} will returned as Optional.
     * Otherwise an empty Optional is returned.
     *
     * @param event meta data instance for {@link LoggingEvent}
     * @param logRequest {@link LogRequest} object to apply strategy for
     * @return created {@link BooleanExpression} as Optional, or empty Optional if not applicable
     */
    Optional<BooleanExpression> applyStrategy(QLoggingEvent event, LogRequest logRequest);
}

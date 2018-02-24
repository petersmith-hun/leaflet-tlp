package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * {@link ExpressionStrategy} implementation that handles {@link LogRequest#from} and {@link LogRequest#to} filter.
 *
 * Only those of the two fields will be used in filtering, which is not null.
 * If both is given, it will be interpreted as "between" filtering.
 * From field alone will be filtered as "after".
 * To field alone will be filtered as "before".
 *
 * @author Peter Smith
 */
@Component
public class TimestampExpressionStrategy implements ExpressionStrategy {

    @Override
    public Optional<BooleanExpression> applyStrategy(QLoggingEvent event, LogRequest logRequest) {

        BooleanExpression expression = null;
        if (Objects.nonNull(logRequest.getFrom()) && Objects.nonNull(logRequest.getTo())) {
            expression = event.timeStamp.between(logRequest.getFrom(), logRequest.getTo());
        } else if (Objects.nonNull(logRequest.getFrom())) {
            expression = event.timeStamp.after(logRequest.getFrom());
        } else if (Objects.nonNull(logRequest.getTo())) {
            expression = event.timeStamp.before(logRequest.getTo());
        }

        return Optional.ofNullable(expression);
    }
}

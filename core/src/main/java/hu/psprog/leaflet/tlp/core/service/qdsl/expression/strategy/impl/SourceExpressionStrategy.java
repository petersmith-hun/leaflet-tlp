package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * {@link ExpressionStrategy} implementation that handles {@link LogRequest#source} filter.
 *
 * Expression will be created, if source field is not null and not empty.
 * Case will be ignored.
 *
 * @author Peter Smith
 */
@Component
public class SourceExpressionStrategy implements ExpressionStrategy {

    @Override
    public Optional<BooleanExpression> applyStrategy(QLoggingEvent event, LogRequest logRequest) {

        BooleanExpression expression = null;
        if (Objects.nonNull(logRequest.getSource()) && !StringUtils.EMPTY.equals(logRequest.getSource())) {
            expression = event.source.equalsIgnoreCase(logRequest.getSource());
        }

        return Optional.ofNullable(expression);
    }
}

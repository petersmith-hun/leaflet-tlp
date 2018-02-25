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
 * {@link ExpressionStrategy} implementation to handle {@link LogRequest#level} filter.
 *
 * Expression will be created, if level field is not null.
 * Case will be ignored.
 *
 * @author Peter Smith
 */
@Component
public class LevelExpressionStrategy implements ExpressionStrategy {

    @Override
    public Optional<BooleanExpression> applyStrategy(QLoggingEvent event, LogRequest logRequest) {

        BooleanExpression expression = null;
        if (Objects.nonNull(logRequest.getLevel()) && !StringUtils.EMPTY.equals(logRequest.getLevel())) {
            expression = event.level.equalsIgnoreCase(logRequest.getLevel());
        }

        return Optional.ofNullable(expression);
    }
}

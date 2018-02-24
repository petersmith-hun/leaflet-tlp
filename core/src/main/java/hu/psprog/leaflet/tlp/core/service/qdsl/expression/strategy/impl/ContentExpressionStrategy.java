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
 * {@link ExpressionStrategy} implementation that handles {@link LogRequest#content} filter.
 *
 * Expression will be created, if content field is not null and not empty.
 * Value of content field will be looked up in message, exception message and stacktrace of log events.
 * Case will be ignored for all the fields.
 *
 * @author Peter Smith
 */
@Component
public class ContentExpressionStrategy implements ExpressionStrategy {

    @Override
    public Optional<BooleanExpression> applyStrategy(QLoggingEvent event, LogRequest logRequest) {

        BooleanExpression expression = null;
        if (Objects.nonNull(logRequest.getContent()) && !StringUtils.EMPTY.equals(logRequest.getContent())) {
            expression = event.content.containsIgnoreCase(logRequest.getContent())
                    .or(event.exception.message.containsIgnoreCase(logRequest.getContent()))
                    .or(event.exception.stackTrace.containsIgnoreCase(logRequest.getContent()));
        }

        return Optional.ofNullable(expression);
    }
}

package hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Utility that can build a QueryDSL filter expression chain.
 * It requires {@link ExpressionStrategy} implementations that can decide whether to add an expression to the expression chain or not.
 *
 * @author Peter Smith
 */
@Component
public class ExpressionBuilder {

    private static final String LOGGING_EVENT_EXPRESSION_VARIABLE = "event";

    private List<ExpressionStrategy> expressionStrategyList;

    @Autowired
    public ExpressionBuilder(List<ExpressionStrategy> expressionStrategyList) {
        this.expressionStrategyList = expressionStrategyList;
    }

    /**
     * Builds the filter expression chain based on given {@link LogRequest}.
     * Appends the expression returned by a strategy to the chain if the expression is present.
     *
     * @param logRequest {@link LogRequest} that is used as a base for expression chain
     * @return built expression chain, if any strategy has been applied, or empty Optional if none
     */
    public Optional<Predicate> build(LogRequest logRequest) {

        QLoggingEvent event = new QLoggingEvent(LOGGING_EVENT_EXPRESSION_VARIABLE);
        BooleanBuilder expression = new BooleanBuilder();
        expressionStrategyList
                .forEach(strategy -> strategy.applyStrategy(event, logRequest)
                        .ifPresent(expression::and));

        return Optional.ofNullable(expression.getValue());
    }
}

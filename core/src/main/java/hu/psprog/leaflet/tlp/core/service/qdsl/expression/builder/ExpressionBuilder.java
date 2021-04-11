package hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLConditionGroup;
import hu.psprog.leaflet.tlql.ir.DSLLogicalOperator;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility that can build a QueryDSL filter expression chain.
 * It requires {@link ExpressionStrategy} implementations able to process different kinds of {@link DSLCondition} objects.
 *
 * @author Peter Smith
 */
@Component
public class ExpressionBuilder {

    private static final String LOGGING_EVENT_EXPRESSION_VARIABLE = "event";

    private final Map<ExpressionStrategyGroup, ExpressionStrategy> expressionStrategyMap;

    @Autowired
    public ExpressionBuilder(List<ExpressionStrategy> expressionStrategyList) {
        this.expressionStrategyMap = expressionStrategyList.stream()
                .collect(Collectors.toMap(ExpressionStrategy::forGroup, Function.identity()));
    }

    /**
     * Builds the filter expression chain based on given {@link DSLQueryModel}.
     * Appends the expression returned by a strategy to the chain if the expression is present.
     *
     * @param dslQueryModel {@link DSLQueryModel} query intermediate representation object
     * @return built expression chain, if any strategy has been applied, or empty Optional if none
     */
    public Optional<Predicate> build(DSLQueryModel dslQueryModel) {

        QLoggingEvent event = new QLoggingEvent(LOGGING_EVENT_EXPRESSION_VARIABLE);
        BooleanBuilder expression = new BooleanBuilder();
        DSLLogicalOperator previousGroupOperator = DSLLogicalOperator.AND;

        for (DSLConditionGroup dslConditionGroup : dslQueryModel.getConditionGroups()) {

            DSLLogicalOperator previousOperator = DSLLogicalOperator.AND;
            BooleanBuilder groupExpression = new BooleanBuilder();

            for (DSLCondition dslCondition : dslConditionGroup.getConditions()) {
                ExpressionStrategyGroup group = ExpressionStrategyGroup.getByApplicationDSLObject(dslCondition.getObject());
                BooleanExpression conditionExpression = expressionStrategyMap.get(group).applyStrategy(event, dslCondition);

                chainExpression(groupExpression, previousOperator, conditionExpression);
                previousOperator = dslCondition.getNextConditionOperator();
            }

            chainExpression(expression, previousGroupOperator, groupExpression);
            previousGroupOperator = dslConditionGroup.getNextConditionGroupOperator();
        }

        return Optional.ofNullable(expression.getValue());
    }

    private void chainExpression(BooleanBuilder expressionGroup, DSLLogicalOperator previousGroupOperator, Predicate currentExpression) {

        if (previousGroupOperator == DSLLogicalOperator.AND) {
            expressionGroup.and(currentExpression);
        } else if (previousGroupOperator == DSLLogicalOperator.OR) {
            expressionGroup.or(currentExpression);
        }
    }
}

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
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link ExpressionBuilder}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ExpressionBuilderTest {

    private static final QLoggingEvent Q_LOGGING_EVENT = new QLoggingEvent("event");
    private static final BooleanExpression CONTENT_EXPRESSION = Q_LOGGING_EVENT.content.eq("test-content");
    private static final BooleanExpression SOURCE_EXPRESSION = Q_LOGGING_EVENT.source.eq("test-source");
    private static final BooleanExpression TIMESTAMP_EXPRESSION = Q_LOGGING_EVENT.timeStamp.eq(new Date());

    @Mock(strictness = Mock.Strictness.LENIENT)
    private ExpressionStrategy textConditionExpressionStrategy;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private ExpressionStrategy timestampConditionExpressionStrategy;

    private ExpressionBuilder expressionBuilder;

    @BeforeEach
    public void setup() {
        given(textConditionExpressionStrategy.applyStrategy(any(), any()))
                .willReturn(CONTENT_EXPRESSION)
                .willReturn(SOURCE_EXPRESSION);
        given(textConditionExpressionStrategy.forGroup()).willReturn(ExpressionStrategyGroup.TEXT_CONDITION);
        given(timestampConditionExpressionStrategy.applyStrategy(any(), any()))
                .willReturn(TIMESTAMP_EXPRESSION);
        given(timestampConditionExpressionStrategy.forGroup()).willReturn(ExpressionStrategyGroup.TIMESTAMP_CONDITION);

        expressionBuilder = new ExpressionBuilder(Arrays.asList(textConditionExpressionStrategy, timestampConditionExpressionStrategy));
    }

    @Test
    public void shouldBuildSingleExpression() {

        // given
        DSLQueryModel dslQueryModel = prepareDSLQueryModel(1);
        addCondition(dslQueryModel, 0, DSLObject.MESSAGE);

        // when
        Optional<Predicate> result = expressionBuilder.build(dslQueryModel);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().toString(), equalTo(CONTENT_EXPRESSION.toString()));
    }

    @Test
    public void shouldBuildExpressionChain() {

        // given
        DSLQueryModel dslQueryModel = prepareDSLQueryModel(1);
        addCondition(dslQueryModel, 0, DSLObject.MESSAGE, DSLLogicalOperator.AND);
        addCondition(dslQueryModel, 0, DSLObject.TIMESTAMP, null);
        Predicate expectedPredicate = new BooleanBuilder()
                .and(CONTENT_EXPRESSION)
                .and(TIMESTAMP_EXPRESSION)
                .getValue();

        // when
        Optional<Predicate> result = expressionBuilder.build(dslQueryModel);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().toString(), equalTo(expectedPredicate.toString()));
    }

    @Test
    public void shouldBuildMultiGroupExpressionChainWithAndGroupRelation() {

        // given
        DSLQueryModel dslQueryModel = prepareDSLQueryModel(2, DSLLogicalOperator.AND);
        addCondition(dslQueryModel, 0, DSLObject.MESSAGE, DSLLogicalOperator.OR);
        addCondition(dslQueryModel, 0, DSLObject.TIMESTAMP, null);
        addCondition(dslQueryModel, 1, DSLObject.SOURCE);
        Predicate expectedPredicate = new BooleanBuilder()
                .and(new BooleanBuilder()
                        .and(CONTENT_EXPRESSION)
                        .or(TIMESTAMP_EXPRESSION))
                .and(SOURCE_EXPRESSION)
                .getValue();

        // when
        Optional<Predicate> result = expressionBuilder.build(dslQueryModel);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().toString(), equalTo(expectedPredicate.toString()));
    }

    @Test
    public void shouldBuildMultiGroupExpressionChainWithOrGroupRelation() {

        // given
        DSLQueryModel dslQueryModel = prepareDSLQueryModel(2, DSLLogicalOperator.OR);
        addCondition(dslQueryModel, 0, DSLObject.MESSAGE, DSLLogicalOperator.AND);
        addCondition(dslQueryModel, 0, DSLObject.TIMESTAMP, null);
        addCondition(dslQueryModel, 1, DSLObject.SOURCE, DSLLogicalOperator.AND);
        addCondition(dslQueryModel, 1, DSLObject.TIMESTAMP, null);
        Predicate expectedPredicate = new BooleanBuilder()
                .and(new BooleanBuilder()
                        .and(CONTENT_EXPRESSION)
                        .and(TIMESTAMP_EXPRESSION))
                .or(new BooleanBuilder()
                        .and(SOURCE_EXPRESSION)
                        .and(TIMESTAMP_EXPRESSION))
                .getValue();

        // when
        Optional<Predicate> result = expressionBuilder.build(dslQueryModel);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().toString(), equalTo(expectedPredicate.toString()));
    }

    @Test
    public void shouldBuildEmptyExpression() {

        // given
        DSLQueryModel dslQueryModel = prepareDSLQueryModel(0);

        // when
        Optional<Predicate> result = expressionBuilder.build(dslQueryModel);

        // then
        assertThat(result.isPresent(), is(false));
    }

    private DSLQueryModel prepareDSLQueryModel(int numberOfGroups) {
        return prepareDSLQueryModel(numberOfGroups, null);
    }

    private DSLQueryModel prepareDSLQueryModel(int numberOfGroups, DSLLogicalOperator groupJoinOperator) {

        DSLQueryModel dslQueryModel = new DSLQueryModel();
        for (int groupIndex = 0; groupIndex < numberOfGroups; groupIndex++) {
            DSLConditionGroup conditionGroup = new DSLConditionGroup();
            if (groupIndex < numberOfGroups - 1) {
                conditionGroup.setNextConditionGroupOperator(groupJoinOperator);
            }
            dslQueryModel.getConditionGroups().add(conditionGroup);
        }

        return dslQueryModel;
    }

    private void addCondition(DSLQueryModel dslQueryModel, int groupIndex, DSLObject object) {
        addCondition(dslQueryModel, groupIndex, object, null);
    }

    private void addCondition(DSLQueryModel dslQueryModel, int groupIndex, DSLObject object, DSLLogicalOperator nextOperator) {

        DSLCondition dslCondition = new DSLCondition();
        dslCondition.setObject(object);
        dslCondition.setNextConditionOperator(nextOperator);

        dslQueryModel.getConditionGroups().get(groupIndex).getConditions().add(dslCondition);
    }
}
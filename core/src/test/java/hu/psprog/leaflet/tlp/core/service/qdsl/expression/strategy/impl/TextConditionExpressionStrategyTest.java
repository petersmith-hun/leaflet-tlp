package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.ExpressionStrategyGroup;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.MappingRegistry;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLObjectContext;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link TextConditionExpressionStrategy}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class TextConditionExpressionStrategyTest {

    @Mock
    private MappingRegistry<StringPath, String> mappingRegistry;

    @InjectMocks
    private TextConditionExpressionStrategy textConditionExpressionStrategy;

    @Test
    public void shouldApplyStrategyGenerateSingleValueExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition(DSLObject.MESSAGE, DSLOperator.LIKE, "Some log message");
        Function<QLoggingEvent, StringPath> stringPathFunction = loggingEvent -> loggingEvent.content;
        BiFunction<StringPath, String, BooleanExpression> booleanExpressionBiFunction = StringExpression::containsIgnoreCase;

        given(mappingRegistry.getQDSLPath(DSLObject.MESSAGE)).willReturn(stringPathFunction);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.LIKE)).willReturn(booleanExpressionBiFunction);

        // when
        BooleanExpression result = textConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("containsIc(loggingEvent.content,Some log message)"));
    }

    @Test
    public void shouldApplyStrategyGenerateMultiValueExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition(DSLObject.SOURCE, DSLOperator.EITHER, "lcfa", "leaflet", "cbfs");
        Function<QLoggingEvent, StringPath> stringPathFunction = loggingEvent -> loggingEvent.source;
        BiFunction<StringPath, String[], BooleanExpression> booleanExpressionBiFunction = StringExpression::in;

        given(mappingRegistry.getQDSLPath(DSLObject.SOURCE)).willReturn(stringPathFunction);
        given(mappingRegistry.getMultiValueExpressionGenerator(DSLOperator.EITHER)).willReturn(booleanExpressionBiFunction);

        // when
        BooleanExpression result = textConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.source in [lcfa, leaflet, cbfs]"));
    }

    @Test
    public void shouldApplyStrategyGenerateMultiValueLevelFieldExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition(DSLObject.LEVEL, DSLOperator.NONE, "info", "warn", "error");
        Function<QLoggingEvent, StringPath> stringPathFunction = loggingEvent -> loggingEvent.level;
        BiFunction<StringPath, String[], BooleanExpression> booleanExpressionBiFunction = StringExpression::notIn;

        given(mappingRegistry.getQDSLPath(DSLObject.LEVEL)).willReturn(stringPathFunction);
        given(mappingRegistry.getMultiValueExpressionGenerator(DSLOperator.NONE)).willReturn(booleanExpressionBiFunction);

        // when
        BooleanExpression result = textConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        assertThat(result.toString(), equalTo("loggingEvent.level not in [INFO, WARN, ERROR]"));
    }

    @Test
    public void shouldForGroupReturnTextConditionExpressionStrategyGroup() {

        // when
        ExpressionStrategyGroup result = textConditionExpressionStrategy.forGroup();

        // then
        assertThat(result, equalTo(ExpressionStrategyGroup.TEXT_CONDITION));
    }

    private DSLCondition prepareDSLCondition(DSLObject dslObject, DSLOperator dslOperator, String... values) {

        DSLCondition dslCondition = new DSLCondition();
        dslCondition.setObjectContext(new DSLObjectContext(dslObject, null));
        dslCondition.setOperator(dslOperator);

        if (values.length > 1) {
            dslCondition.setMultipleValue(Arrays.asList(values));
        } else {
            dslCondition.setValue(values[0]);
        }

        return dslCondition;
    }
}

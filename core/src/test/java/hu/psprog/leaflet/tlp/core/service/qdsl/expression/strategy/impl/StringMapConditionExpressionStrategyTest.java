package hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.MapPath;
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
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link StringMapConditionExpressionStrategy}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class StringMapConditionExpressionStrategyTest {

    @Mock
    private MappingRegistry<MapPath<String, String, StringPath>, Map<String, String>> mappingRegistry;

    @Mock
    private BiFunction<MapPath<String, String, StringPath>, Map<String, String>, BooleanExpression> singleValueExpression;

    @Mock
    private BiFunction<MapPath<String, String, StringPath>, Map<String, String>[], BooleanExpression> multiValueExpression;

    @InjectMocks
    private StringMapConditionExpressionStrategy stringMapConditionExpressionStrategy;

    @Test
    public void shouldApplyStrategyGenerateSingleValueContextConditionExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition(DSLOperator.EQUALS, "requestID", "abcd1234");
        Function<QLoggingEvent, MapPath<String, String, StringPath>> stringMapPathFunction = loggingEvent -> loggingEvent.context;

        given(mappingRegistry.getQDSLPath(DSLObject.CONTEXT)).willReturn(stringMapPathFunction);
        given(mappingRegistry.getSingleValueExpressionGenerator(DSLOperator.EQUALS)).willReturn(singleValueExpression);

        // when
        stringMapConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        verify(singleValueExpression).apply(event.context, Map.of("requestID", "abcd1234"));
    }

    @Test
    public void shouldApplyStrategyGenerateMultiValueContextConditionExpression() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        DSLCondition dslCondition = prepareDSLCondition(DSLOperator.EITHER, "trace_id", "abcd1234", "efgh9876");
        Function<QLoggingEvent, MapPath<String, String, StringPath>> stringMapPathFunction = loggingEvent -> loggingEvent.context;

        given(mappingRegistry.getQDSLPath(DSLObject.CONTEXT)).willReturn(stringMapPathFunction);
        given(mappingRegistry.getMultiValueExpressionGenerator(DSLOperator.EITHER)).willReturn(multiValueExpression);

        // when
        stringMapConditionExpressionStrategy.applyStrategy(event, dslCondition);

        // then
        verify(multiValueExpression).apply(event.context, List.of(Map.of("trace_id", "abcd1234"), Map.of("trace_id", "efgh9876")).toArray(Map[]::new));
    }

    @Test
    public void shouldForGroupReturnTextMapConditionExpressionStrategyGroup() {

        // when
        ExpressionStrategyGroup result = stringMapConditionExpressionStrategy.forGroup();

        // then
        assertThat(result, equalTo(ExpressionStrategyGroup.TEXT_MAP_CONDITION));
    }

    private DSLCondition prepareDSLCondition(DSLOperator dslOperator, String specialization, String... values) {

        DSLCondition dslCondition = new DSLCondition();
        dslCondition.setObjectContext(new DSLObjectContext(DSLObject.CONTEXT, specialization));
        dslCondition.setOperator(dslOperator);

        if (values.length > 1) {
            dslCondition.setMultipleValue(Arrays.asList(values));
        } else {
            dslCondition.setValue(values[0]);
        }

        return dslCondition;
    }
}

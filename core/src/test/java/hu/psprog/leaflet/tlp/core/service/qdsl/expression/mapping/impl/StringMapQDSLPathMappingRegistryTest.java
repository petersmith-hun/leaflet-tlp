package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link StringMapQDSLPathMappingRegistry}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class StringMapQDSLPathMappingRegistryTest {

    @InjectMocks
    private StringMapQDSLPathMappingRegistry stringMapQDSLPathMappingRegistry;

    @Test
    public void shouldGetQDSLPathReturnContextPath() {

        // given
        QLoggingEvent loggingEvent = new QLoggingEvent("loggingEvent");
        DSLObject dslObject = DSLObject.CONTEXT;

        // when
        Function<QLoggingEvent, MapPath<String, String, StringPath>> result = stringMapQDSLPathMappingRegistry.getQDSLPath(dslObject);

        // then
        assertThat(result.apply(loggingEvent).toString(), equalTo("loggingEvent.context"));
    }

    @ParameterizedTest
    @EnumSource(value = DSLObject.class, mode = EnumSource.Mode.EXCLUDE, names = "CONTEXT")
    public void shouldGetQDSLPathThrowErrorIfObjectIsNotContext(DSLObject dslObject) {

        // when
        assertThrows(IllegalArgumentException.class, () -> stringMapQDSLPathMappingRegistry.getQDSLPath(dslObject));

        // then
        // exception expected
    }

    @ParameterizedTest
    @MethodSource("singleValueExpressionGeneratorArgumentsProvider")
    public void shouldGetSingleValueExpressionGeneratorReturnTheCorrespondingMapperFunction(DSLOperator dslOperator, String expectedExpression) {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        Map<String, String> valueMap = Map.of("requestID", "abcd1234");

        // when
        BiFunction<MapPath<String, String, StringPath>, Map<String, String>, BooleanExpression> result =
                stringMapQDSLPathMappingRegistry.getSingleValueExpressionGenerator(dslOperator);

        // then
        assertThat(result.apply(event.context, valueMap).toString(), equalTo(expectedExpression));
    }

    @ParameterizedTest
    @MethodSource("multiValueExpressionGeneratorArgumentsProvider")
    public void shouldGetMultiValueExpressionGeneratorReturnTheCorrespondingMapperFunction(DSLOperator dslOperator, String expectedExpression) {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");
        Map<String, String>[] valueMap = List.of(Map.of("trace_id", "abcd1234"), Map.of("trace_id", "efgh9876")).toArray(Map[]::new);

        // when
        BiFunction<MapPath<String, String, StringPath>, Map<String, String>[], BooleanExpression> result =
                stringMapQDSLPathMappingRegistry.getMultiValueExpressionGenerator(dslOperator);

        // then
        assertThat(result.apply(event.context, valueMap).toString(), equalTo(expectedExpression));
    }

    private static Stream<Arguments> singleValueExpressionGeneratorArgumentsProvider() {

        return Stream.of(
                Arguments.of(DSLOperator.EQUALS, "loggingEvent.context.get(requestID) = abcd1234"),
                Arguments.of(DSLOperator.NOT_EQUALS, "!(loggingEvent.context.get(requestID) = abcd1234)"),
                Arguments.of(DSLOperator.LIKE, "containsIc(loggingEvent.context.get(requestID),abcd1234)")
        );
    }

    private static Stream<Arguments> multiValueExpressionGeneratorArgumentsProvider() {

        return Stream.of(
                Arguments.of(DSLOperator.EITHER, "loggingEvent.context.get(trace_id) = abcd1234 || loggingEvent.context.get(trace_id) = efgh9876"),
                Arguments.of(DSLOperator.NONE, "!(loggingEvent.context.get(trace_id) = abcd1234 || loggingEvent.context.get(trace_id) = efgh9876)")
        );
    }
}

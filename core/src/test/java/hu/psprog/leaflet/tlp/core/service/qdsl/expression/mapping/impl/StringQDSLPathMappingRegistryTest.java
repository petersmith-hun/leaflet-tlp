package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link StringQDSLPathMappingRegistry}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class StringQDSLPathMappingRegistryTest {

    @InjectMocks
    private StringQDSLPathMappingRegistry mappingRegistry;

    @ParameterizedTest
    @MethodSource("qdslPathArgumentsProvider")
    public void shouldGetQDSLPathReturnTheCorrespondingMapperFunction(DSLObject dslObject, String expectedFieldName) {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");

        // when
        Function<QLoggingEvent, StringPath> result = mappingRegistry.getQDSLPath(dslObject);

        // then
        assertThat(result.apply(event).getMetadata().getName(), equalTo(expectedFieldName));
    }

    @Test
    public void shouldGetQDSLPathThrowExceptionForInvalidDSLObject() {

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP));

        // then
        // exception expected
    }

    @ParameterizedTest
    @MethodSource("singleValueExpressionGeneratorArgumentsProvider")
    public void shouldGetSingleValueExpressionGeneratorReturnTheCorrespondingMapperFunction(DSLOperator dslOperator, String expectedExpression) {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");

        // when
        BiFunction<StringPath, String, BooleanExpression> result = mappingRegistry.getSingleValueExpressionGenerator(dslOperator);

        // then
        assertThat(result.apply(event.content, "some_value").toString(), equalTo(expectedExpression));
    }

    @ParameterizedTest
    @MethodSource("multiValueExpressionGeneratorArgumentsProvider")
    public void shouldGetMultiValueExpressionGeneratorReturnTheCorrespondingMapperFunction(DSLOperator dslOperator, String expectedExpression) {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");

        // when
        BiFunction<StringPath, String[], BooleanExpression> result = mappingRegistry.getMultiValueExpressionGenerator(dslOperator);

        // then
        assertThat(result.apply(event.level, new String[]{"info", "warn"}).toString(), equalTo(expectedExpression));
    }

    private static Stream<Arguments> qdslPathArgumentsProvider() {

        return Stream.of(
                Arguments.of(DSLObject.SOURCE, "source"),
                Arguments.of(DSLObject.LEVEL, "level"),
                Arguments.of(DSLObject.LOGGER, "loggerName"),
                Arguments.of(DSLObject.MESSAGE, "content"),
                Arguments.of(DSLObject.THREAD, "threadName")
        );
    }

    private static Stream<Arguments> singleValueExpressionGeneratorArgumentsProvider() {

        return Stream.of(
                Arguments.of(DSLOperator.EQUALS, "eqIc(loggingEvent.content,some_value)"),
                Arguments.of(DSLOperator.NOT_EQUALS, "!(eqIc(loggingEvent.content,some_value))"),
                Arguments.of(DSLOperator.LIKE, "containsIc(loggingEvent.content,some_value)")
        );
    }

    private static Stream<Arguments> multiValueExpressionGeneratorArgumentsProvider() {

        return Stream.of(
                Arguments.of(DSLOperator.EITHER, "loggingEvent.level in [info, warn]"),
                Arguments.of(DSLOperator.NONE, "loggingEvent.level not in [info, warn]")
        );
    }
}

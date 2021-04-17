package hu.psprog.leaflet.tlp.core.service.qdsl.expression.mapping.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link DateQDSLPathMappingRegistry}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class DateQDSLPathMappingRegistryTest {

    private static final Date DATE_EXPRESSION = Timestamp.valueOf(LocalDateTime.of(LocalDate.of(2021, 4, 14), LocalTime.of(23, 0)));

    @InjectMocks
    private DateQDSLPathMappingRegistry mappingRegistry;

    @Test
    public void shouldGetQDSLPathReturnTheCorrespondingMapperFunction() {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");

        // when
        Function<QLoggingEvent, DateTimePath<Date>> result = mappingRegistry.getQDSLPath(DSLObject.TIMESTAMP);

        // then
        assertThat(result.apply(event).getMetadata().getName(), equalTo("timeStamp"));
    }

    @ParameterizedTest
    @EnumSource(value = DSLObject.class, names = "TIMESTAMP", mode = EnumSource.Mode.EXCLUDE)
    public void shouldGetQDSLPathThrowExceptionForInvalidDSLObject(DSLObject dslObject) {

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> mappingRegistry.getQDSLPath(dslObject));

        // then
        // exception expected
    }

    @ParameterizedTest
    @MethodSource("singleValueExpressionGeneratorArgumentsProvider")
    public void shouldGetSingleValueExpressionGeneratorReturnTheCorrespondingMapperFunction(DSLOperator dslOperator, String expectedExpression) {

        // given
        QLoggingEvent event = new QLoggingEvent("loggingEvent");

        // when
        BiFunction<DateTimePath<Date>, Date, BooleanExpression> result = mappingRegistry.getSingleValueExpressionGenerator(dslOperator);

        // then
        assertThat(result.apply(event.timeStamp, DATE_EXPRESSION).toString(), equalTo(expectedExpression));
    }

    @Test
    public void shouldGetMultiValueExpressionGeneratorThrowUnsupportedOperationException() {

        // when
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> mappingRegistry.getMultiValueExpressionGenerator(DSLOperator.EQUALS));

        // then
        // exception expected
    }

    private static Stream<Arguments> singleValueExpressionGeneratorArgumentsProvider() {

        return Stream.of(
                Arguments.of(DSLOperator.EQUALS, "loggingEvent.timeStamp = 2021-04-14 23:00:00.0"),
                Arguments.of(DSLOperator.NOT_EQUALS, "loggingEvent.timeStamp != 2021-04-14 23:00:00.0"),
                Arguments.of(DSLOperator.GREATER_THAN, "loggingEvent.timeStamp > 2021-04-14 23:00:00.0"),
                Arguments.of(DSLOperator.GREATER_THAN_OR_EQUAL, "loggingEvent.timeStamp >= 2021-04-14 23:00:00.0"),
                Arguments.of(DSLOperator.LESS_THAN, "loggingEvent.timeStamp < 2021-04-14 23:00:00.0"),
                Arguments.of(DSLOperator.LESS_THAN_OR_EQUAL, "loggingEvent.timeStamp <= 2021-04-14 23:00:00.0")

        );
    }
}

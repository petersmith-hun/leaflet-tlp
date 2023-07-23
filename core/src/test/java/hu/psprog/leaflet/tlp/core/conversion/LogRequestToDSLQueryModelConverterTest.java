package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.api.domain.OrderBy;
import hu.psprog.leaflet.tlp.api.domain.OrderDirection;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLConditionGroup;
import hu.psprog.leaflet.tlql.ir.DSLLogicalOperator;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLObjectContext;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import hu.psprog.leaflet.tlql.ir.DSLOrderDirection;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import hu.psprog.leaflet.tlql.ir.DSLTimestampValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link LogRequestToDSLQueryModelConverter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class LogRequestToDSLQueryModelConverterTest {

    private static final Date FROM_DATE = Date.from(Instant.parse("2021-04-10T00:00:00.00Z"));
    private static final Date TO_DATE = Date.from(Instant.parse("2021-04-15T00:00:00.00Z"));

    @InjectMocks
    private LogRequestToDSLQueryModelConverter converter;

    @Test
    public void shouldConvertCreateDSLQueryModelBasedOnDefaultLogRequest() {

        // given
        LogRequest logRequest = new LogRequest();

        DSLQueryModel expectedDslQueryModel = new DSLQueryModel();
        setDefaultPagination(expectedDslQueryModel);

        // when
        DSLQueryModel result = converter.convert(logRequest);

        // then
        assertThat(result, equalTo(expectedDslQueryModel));
    }

    @Test
    public void shouldConvertCreateDSLQueryModelBasedOnLogRequestWithExplicitPagination() {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setLimit(30);
        logRequest.setPage(4);
        logRequest.setOrderBy(OrderBy.LEVEL);
        logRequest.setOrderDirection(OrderDirection.ASC);

        DSLQueryModel expectedDslQueryModel = new DSLQueryModel();
        expectedDslQueryModel.setOffset(90);
        expectedDslQueryModel.setLimit(30);
        expectedDslQueryModel.getOrdering().put(DSLObject.LEVEL, DSLOrderDirection.ASC);

        // when
        DSLQueryModel result = converter.convert(logRequest);

        // then
        assertThat(result, equalTo(expectedDslQueryModel));
    }

    @Test
    public void shouldConvertCreateDSLQueryModelBasedOnLogRequestWithOneFilterExpression() {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setContent("some message to be queried");
        logRequest.setLevel("");

        DSLQueryModel expectedDslQueryModel = new DSLQueryModel();
        expectedDslQueryModel.getConditionGroups().add(new DSLConditionGroup());
        setDefaultPagination(expectedDslQueryModel);

        DSLCondition messageCondition = new DSLCondition();
        messageCondition.setObjectContext(new DSLObjectContext(DSLObject.MESSAGE, null));
        messageCondition.setOperator(DSLOperator.LIKE);
        messageCondition.setValue(logRequest.getContent());
        expectedDslQueryModel.getConditionGroups().get(0).getConditions().add(messageCondition);

        // when
        DSLQueryModel result = converter.convert(logRequest);

        // then
        assertThat(result, equalTo(expectedDslQueryModel));
    }

    @Test
    public void shouldConvertCreateDSLQueryModelBasedOnLogRequestWithMultipleFilterExpression() {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setContent("some message to be queried");
        logRequest.setSource("lcfa");
        logRequest.setLevel("ERROR");
        logRequest.setFrom(FROM_DATE);

        DSLQueryModel expectedDslQueryModel = new DSLQueryModel();
        expectedDslQueryModel.getConditionGroups().add(new DSLConditionGroup());
        setDefaultPagination(expectedDslQueryModel);

        DSLCondition messageCondition = new DSLCondition();
        messageCondition.setObjectContext(new DSLObjectContext(DSLObject.MESSAGE, null));
        messageCondition.setOperator(DSLOperator.LIKE);
        messageCondition.setValue(logRequest.getContent());
        messageCondition.setNextConditionOperator(DSLLogicalOperator.AND);

        DSLCondition sourceCondition = new DSLCondition();
        sourceCondition.setObjectContext(new DSLObjectContext(DSLObject.SOURCE, null));
        sourceCondition.setOperator(DSLOperator.EQUALS);
        sourceCondition.setValue(logRequest.getSource());
        sourceCondition.setNextConditionOperator(DSLLogicalOperator.AND);

        DSLCondition levelCondition = new DSLCondition();
        levelCondition.setObjectContext(new DSLObjectContext(DSLObject.LEVEL, null));
        levelCondition.setOperator(DSLOperator.EQUALS);
        levelCondition.setValue(logRequest.getLevel());
        levelCondition.setNextConditionOperator(DSLLogicalOperator.AND);

        DSLCondition fromCondition = new DSLCondition();
        fromCondition.setObjectContext(new DSLObjectContext(DSLObject.TIMESTAMP, null));
        fromCondition.setOperator(DSLOperator.GREATER_THAN);
        fromCondition.setTimestampValue(new DSLTimestampValue(prepareLocalDateTime("2021-04-10")));

        expectedDslQueryModel.getConditionGroups().get(0).getConditions()
                .addAll(Arrays.asList(messageCondition, sourceCondition, levelCondition, fromCondition));

        // when
        DSLQueryModel result = converter.convert(logRequest);

        // then
        assertThat(result, equalTo(expectedDslQueryModel));
    }

    @Test
    public void shouldConvertCreateDSLQueryModelBasedOnLogRequestWithToTimestampFilterExpression() {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setTo(TO_DATE);

        DSLQueryModel expectedDslQueryModel = new DSLQueryModel();
        expectedDslQueryModel.getConditionGroups().add(new DSLConditionGroup());
        setDefaultPagination(expectedDslQueryModel);

        DSLCondition toCondition = new DSLCondition();
        toCondition.setObjectContext(new DSLObjectContext(DSLObject.TIMESTAMP, null));
        toCondition.setOperator(DSLOperator.LESS_THAN);
        toCondition.setTimestampValue(new DSLTimestampValue(prepareLocalDateTime("2021-04-15")));

        expectedDslQueryModel.getConditionGroups().get(0).getConditions().add(toCondition);

        // when
        DSLQueryModel result = converter.convert(logRequest);

        // then
        assertThat(result, equalTo(expectedDslQueryModel));
    }

    @Test
    public void shouldConvertCreateDSLQueryModelBasedOnLogRequestWithBetweenTimestampFilterExpression() {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setFrom(FROM_DATE);
        logRequest.setTo(TO_DATE);

        DSLQueryModel expectedDslQueryModel = new DSLQueryModel();
        expectedDslQueryModel.getConditionGroups().add(new DSLConditionGroup());
        setDefaultPagination(expectedDslQueryModel);

        DSLCondition toCondition = new DSLCondition();
        toCondition.setObjectContext(new DSLObjectContext(DSLObject.TIMESTAMP, null));
        toCondition.setOperator(DSLOperator.BETWEEN);
        toCondition.setTimestampValue(new DSLTimestampValue(DSLTimestampValue.IntervalType.FULL_EXCLUSIVE,
                prepareLocalDateTime("2021-04-10"), prepareLocalDateTime("2021-04-15")));

        expectedDslQueryModel.getConditionGroups().get(0).getConditions().add(toCondition);

        // when
        DSLQueryModel result = converter.convert(logRequest);

        // then
        assertThat(result, equalTo(expectedDslQueryModel));
    }

    private void setDefaultPagination(DSLQueryModel expectedDslQueryModel) {
        expectedDslQueryModel.setOffset(0);
        expectedDslQueryModel.setLimit(50);
        expectedDslQueryModel.getOrdering().put(DSLObject.TIMESTAMP, DSLOrderDirection.DESC);
    }

    private LocalDateTime prepareLocalDateTime(String dateTimeString) {
        return LocalDateTime.of(LocalDate.parse(dateTimeString), LocalTime.MIDNIGHT);
    }
}

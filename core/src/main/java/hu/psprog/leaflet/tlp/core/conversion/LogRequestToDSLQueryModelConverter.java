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
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Converts a TLP API v1 {@link LogRequest} object to a TLP API v2 {@link DSLQueryModel} object.
 *
 * @author Peter Smith
 */
@Component
public class LogRequestToDSLQueryModelConverter implements Converter<LogRequest, DSLQueryModel> {

    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private static final Map<OrderBy, DSLObject> ORDER_BY_TO_DSL_OBJECT_ENUM_MAP = Map.of(
            OrderBy.CONTENT, DSLObject.MESSAGE,
            OrderBy.LEVEL, DSLObject.LEVEL,
            OrderBy.TIMESTAMP, DSLObject.TIMESTAMP
    );
    private static final Map<OrderDirection, DSLOrderDirection> ORDER_DIRECTION_ENUM_MAP = Map.of(
            OrderDirection.ASC, DSLOrderDirection.ASC,
            OrderDirection.DESC, DSLOrderDirection.DESC
    );

    private static final Map<DSLObject, Function<LogRequest, String>> DSL_OBJECT_PROVIDER_MAP = createObjectProviderMap();

    @Override
    public DSLQueryModel convert(LogRequest logRequest) {

        DSLQueryModel dslQueryModel = new DSLQueryModel();

        DSLConditionGroup dslConditionGroup = createConditions(logRequest);
        setPaging(logRequest, dslQueryModel);
        setOrdering(logRequest, dslQueryModel);

        if (!dslConditionGroup.getConditions().isEmpty()) {
            dslQueryModel.getConditionGroups().add(dslConditionGroup);
        }

        return dslQueryModel;
    }

    private DSLConditionGroup createConditions(LogRequest logRequest) {

        DSLConditionGroup dslConditionGroup = new DSLConditionGroup();

        DSL_OBJECT_PROVIDER_MAP.forEach((dslObject, mapperFunction) -> {
            String value = mapperFunction.apply(logRequest);
            if (Objects.nonNull(value) && value.length() > 0) {
                setLogicalChain(dslConditionGroup);
                dslConditionGroup.getConditions().add(createSimpleCondition(dslObject, value));
            }
        });

        createTimestampCondition(logRequest).ifPresent(dslCondition -> {
            setLogicalChain(dslConditionGroup);
            dslConditionGroup.getConditions().add(dslCondition);
        });

        return dslConditionGroup;
    }

    private DSLCondition createSimpleCondition(DSLObject dslObject, String value) {

        DSLCondition dslCondition = new DSLCondition();
        dslCondition.setObjectContext(new DSLObjectContext(dslObject, null));
        dslCondition.setOperator(dslObject == DSLObject.MESSAGE
                ? DSLOperator.LIKE
                : DSLOperator.EQUALS);
        dslCondition.setValue(value);

        return dslCondition;
    }

    private Optional<DSLCondition> createTimestampCondition(LogRequest logRequest) {

        DSLCondition dslCondition = null;

        if (Objects.nonNull(logRequest.getFrom()) || Objects.nonNull(logRequest.getTo())) {

            DSLOperator dslOperator;
            DSLTimestampValue dslTimestampValue;
            if (Objects.nonNull(logRequest.getFrom()) && Objects.nonNull(logRequest.getTo())) {
                dslOperator = DSLOperator.BETWEEN;
                dslTimestampValue = new DSLTimestampValue(DSLTimestampValue.IntervalType.FULL_EXCLUSIVE,
                        convertDate(logRequest.getFrom()), convertDate(logRequest.getTo()));
            } else if (Objects.nonNull(logRequest.getFrom())) {
                dslOperator = DSLOperator.GREATER_THAN;
                dslTimestampValue = new DSLTimestampValue(convertDate(logRequest.getFrom()));
            } else {
                dslOperator = DSLOperator.LESS_THAN;
                dslTimestampValue = new DSLTimestampValue(convertDate(logRequest.getTo()));
            }

            dslCondition = new DSLCondition();
            dslCondition.setObjectContext(new DSLObjectContext(DSLObject.TIMESTAMP, null));
            dslCondition.setOperator(dslOperator);
            dslCondition.setTimestampValue(dslTimestampValue);
        }

        return Optional.ofNullable(dslCondition);
    }

    private LocalDateTime convertDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), UTC_ZONE_ID);
    }

    private void setLogicalChain(DSLConditionGroup dslConditionGroup) {

        dslConditionGroup.getConditions().stream()
                .reduce((dslCondition1, dslCondition2) -> dslCondition2)
                .ifPresent(lastCondition -> lastCondition.setNextConditionOperator(DSLLogicalOperator.AND));
    }

    private void setPaging(LogRequest logRequest, DSLQueryModel dslQueryModel) {

        dslQueryModel.setLimit(logRequest.getLimit());
        dslQueryModel.setOffset(logRequest.getLimit() * (logRequest.getPage() - 1));
    }

    private void setOrdering(LogRequest logRequest, DSLQueryModel dslQueryModel) {

        dslQueryModel.getOrdering().put(
                ORDER_BY_TO_DSL_OBJECT_ENUM_MAP.get(logRequest.getOrderBy()),
                ORDER_DIRECTION_ENUM_MAP.get(logRequest.getOrderDirection())
        );
    }

    private static Map<DSLObject, Function<LogRequest, String>> createObjectProviderMap() {

        Map<DSLObject, Function<LogRequest, String>> objectProviderMap = new LinkedHashMap<>();
        objectProviderMap.put(DSLObject.MESSAGE, LogRequest::getContent);
        objectProviderMap.put(DSLObject.SOURCE, LogRequest::getSource);
        objectProviderMap.put(DSLObject.LEVEL, LogRequest::getLevel);

        return Collections.unmodifiableMap(objectProviderMap);
    }
}

package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.api.domain.OrderBy;
import hu.psprog.leaflet.tlp.api.domain.OrderDirection;
import hu.psprog.leaflet.tlql.ir.DSLCondition;
import hu.psprog.leaflet.tlql.ir.DSLConditionGroup;
import hu.psprog.leaflet.tlql.ir.DSLLogicalOperator;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOperator;
import hu.psprog.leaflet.tlql.ir.DSLOrderDirection;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Converts a TLP API v1 {@link LogRequest} object to a TLP API v2 {@link DSLQueryModel} object.
 *
 * @author Peter Smith
 */
@Component
public class LogRequestToDSLQueryModelConverter implements Converter<LogRequest, DSLQueryModel> {

    private static final Map<OrderBy, DSLObject> ORDER_BY_TO_DSL_OBJECT_ENUM_MAP = Map.of(
            OrderBy.CONTENT, DSLObject.MESSAGE,
            OrderBy.LEVEL, DSLObject.LEVEL,
            OrderBy.TIMESTAMP, DSLObject.TIMESTAMP
    );
    private static final Map<OrderDirection, DSLOrderDirection> ORDER_DIRECTION_ENUM_MAP = Map.of(
            OrderDirection.ASC, DSLOrderDirection.ASC,
            OrderDirection.DESC, DSLOrderDirection.DESC
    );

    private static final Map<DSLObject, Function<LogRequest, String>> DSL_OBJECT_PROVIDER_MAP = Map.of(
            DSLObject.SOURCE, LogRequest::getSource,
            DSLObject.LEVEL, LogRequest::getLevel,
            DSLObject.MESSAGE, LogRequest::getContent
    );

    @Override
    public DSLQueryModel convert(LogRequest logRequest) {

        DSLQueryModel dslQueryModel = new DSLQueryModel();
        DSLConditionGroup dslConditionGroup = new DSLConditionGroup();
        dslQueryModel.getConditionGroups().add(dslConditionGroup);

        setConditions(logRequest, dslConditionGroup);
        setPaging(logRequest, dslQueryModel);
        setOrdering(logRequest, dslQueryModel);

        return dslQueryModel;
    }

    private void setConditions(LogRequest logRequest, DSLConditionGroup dslConditionGroup) {

        DSL_OBJECT_PROVIDER_MAP.forEach((dslObject, mapperFunction) -> {
            String value = mapperFunction.apply(logRequest);
            if (Objects.nonNull(value)) {
                DSLCondition dslCondition = new DSLCondition();
                dslCondition.setObject(dslObject);
                dslCondition.setOperator(DSLOperator.EQUALS);
                dslCondition.setValue(value);

                dslConditionGroup.getConditions().stream()
                        .reduce((dslCondition1, dslCondition2) -> dslCondition2)
                        .ifPresent(lastCondition -> lastCondition.setNextConditionOperator(DSLLogicalOperator.AND));

                dslConditionGroup.getConditions().add(dslCondition);
            }
        });
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
}

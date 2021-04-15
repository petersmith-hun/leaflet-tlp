package hu.psprog.leaflet.tlp.core.conversion;

import com.querydsl.core.types.Path;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlql.ir.DSLObject;
import hu.psprog.leaflet.tlql.ir.DSLOrderDirection;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Extracts pagination information from a {@link DSLQueryModel} object and converts to to {@link Pageable} object.
 *
 * @author Peter Smith
 */
@Component
public class DSLQueryModelToPageRequestConverter implements Converter<DSLQueryModel, Pageable> {

    private static final int DEFAULT_LIMIT = 50;
    private static final Sort DEFAULT_ORDERING = Sort.by(Sort.Direction.DESC, "timeStamp");
    private static final Map<DSLObject, String> DSL_OBJECT_TO_FIELD_NAME_MAP = Map.of(
            DSLObject.SOURCE, getFieldName(QLoggingEvent.loggingEvent.source),
            DSLObject.LEVEL, getFieldName(QLoggingEvent.loggingEvent.level),
            DSLObject.MESSAGE, getFieldName(QLoggingEvent.loggingEvent.content),
            DSLObject.TIMESTAMP, getFieldName(QLoggingEvent.loggingEvent.timeStamp),
            DSLObject.LOGGER, getFieldName(QLoggingEvent.loggingEvent.loggerName)
    );

    @Override
    public Pageable convert(DSLQueryModel dslQueryModel) {

        int limit = dslQueryModel.getLimit() > 0
                ? dslQueryModel.getLimit()
                : DEFAULT_LIMIT;

        return PageRequest.of(getPageNumber(dslQueryModel, limit), limit, mapSorting(dslQueryModel));
    }

    private int getPageNumber(DSLQueryModel dslQueryModel, int limit) {

        // TODO This calculation causes incorrect offset recognition, as it only works in case the offset is exact
        //      multiple of the limit value. Once dropping support for TLP API v1, this should be reworked.

        return dslQueryModel.getOffset() / limit;
    }

    private Sort mapSorting(DSLQueryModel dslQueryModel) {

        return dslQueryModel.getOrdering().entrySet().stream()
                .map(this::mapSingleSort)
                .reduce(Sort::and)
                .orElse(DEFAULT_ORDERING);
    }

    private Sort mapSingleSort(Map.Entry<DSLObject, DSLOrderDirection> orderEntry) {

        Sort.Direction direction = Sort.Direction.valueOf(orderEntry.getValue().name());
        String fieldName = getDatabaseFieldName(orderEntry.getKey());

        return Sort.by(direction, fieldName);
    }

    private static String getFieldName(Path<?> path) {
        return path.getMetadata().getName();
    }

    private static String getDatabaseFieldName(DSLObject dslObject) {
        return DSL_OBJECT_TO_FIELD_NAME_MAP.get(dslObject);
    }
}

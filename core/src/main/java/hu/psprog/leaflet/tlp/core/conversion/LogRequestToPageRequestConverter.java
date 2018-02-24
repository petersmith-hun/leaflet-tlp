package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Extracts paging information from {@link LogRequest} and converts to {@link Pageable}.
 *
 * @author Peter Smith
 */
@Component
public class LogRequestToPageRequestConverter implements Converter<LogRequest, Pageable> {

    @Override
    public Pageable convert(LogRequest source) {
        return new PageRequest(getCorrectedPageNumber(source), source.getLimit(), mapDirection(source), mapOrderBy(source));
    }

    private int getCorrectedPageNumber(LogRequest logRequest) {
        return logRequest.getPage() - 1;
    }

    private Sort.Direction mapDirection(LogRequest logRequest) {
        return Sort.Direction.valueOf(logRequest.getOrderDirection().name());
    }

    private String mapOrderBy(LogRequest logRequest) {
        return logRequest.getOrderBy().getField();
    }
}

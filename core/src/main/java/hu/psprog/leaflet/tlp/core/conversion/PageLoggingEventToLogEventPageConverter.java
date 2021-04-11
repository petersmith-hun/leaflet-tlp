package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Converts {@link Page} of {@link LoggingEvent} objects to {@link LogEventPage}.
 *
 * @author Peter Smith
 */
@Component
public class PageLoggingEventToLogEventPageConverter implements Converter<Page<LoggingEvent>, LogEventPage> {

    private final LoggingEventEntityToDomainConverter loggingEventEntityToDomainConverter;

    @Autowired
    public PageLoggingEventToLogEventPageConverter(LoggingEventEntityToDomainConverter loggingEventEntityToDomainConverter) {
        this.loggingEventEntityToDomainConverter = loggingEventEntityToDomainConverter;
    }

    @Override
    public LogEventPage convert(Page<LoggingEvent> source) {

        return LogEventPage.getBuilder()
                .withPageCount(source.getTotalPages())
                .withPageSize(source.getSize())
                .withPageNumber(source.getNumber() + 1) // Page would count page number from 0, so let's increase by 1
                .withEntityCount(source.getTotalElements())
                .withEntityCountOnPage(source.getNumberOfElements())
                .withFirst(source.isFirst())
                .withLast(source.isLast())
                .withHasNext(source.hasNext())
                .withHasPrevious(source.hasPrevious())
                .withEntitiesOnPage(source.getContent().stream()
                        .map(loggingEventEntityToDomainConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }
}

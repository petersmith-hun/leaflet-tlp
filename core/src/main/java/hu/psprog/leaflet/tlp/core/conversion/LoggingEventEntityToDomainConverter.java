package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.api.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.api.domain.ThrowableProxyLogItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converts Mongo entity {@link LoggingEvent} object to API domain {@link LoggingEvent}.
 *
 * @author Peter Smith
 */
@Component
public class LoggingEventEntityToDomainConverter implements Converter<hu.psprog.leaflet.tlp.core.domain.LoggingEvent, LoggingEvent> {

    @Override
    public LoggingEvent convert(hu.psprog.leaflet.tlp.core.domain.LoggingEvent source) {
        return LoggingEvent.getBuilder()
                .withSource(source.getSource())
                .withTimeStamp(source.getTimeStamp())
                .withContent(source.getContent())
                .withLevel(source.getLevel())
                .withLoggerName(source.getLoggerName())
                .withThreadName(source.getThreadName())
                .withException(Optional.ofNullable(source.getException())
                        .map(this::convert)
                        .orElse(null))
                .withContext(Optional.ofNullable(source.getContext())
                        .orElseGet(Collections::emptyMap))
                .build();
    }

    private ThrowableProxyLogItem convert(hu.psprog.leaflet.tlp.core.domain.ThrowableProxyLogItem source) {
        return ThrowableProxyLogItem.getBuilder()
                .withClassName(source.getClassName())
                .withMessage(source.getMessage())
                .withStackTrace(source.getStackTrace())
                .withCause(extractCause(source))
                .withSuppressed(extractSuppressed(source))
                .build();
    }

    private ThrowableProxyLogItem extractCause(hu.psprog.leaflet.tlp.core.domain.ThrowableProxyLogItem source) {
        return Optional.ofNullable(source.getCause())
                .map(this::convert)
                .orElse(null);
    }

    private List<ThrowableProxyLogItem> extractSuppressed(hu.psprog.leaflet.tlp.core.domain.ThrowableProxyLogItem source) {
        return CollectionUtils.isEmpty(source.getSuppressed())
                ? Collections.emptyList()
                : source.getSuppressed().stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
    }
}

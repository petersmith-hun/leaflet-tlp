package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception (throwableProxy) node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@Data
@JsonDeserialize(builder = ThrowableProxyLogItem.ThrowableProxyBuilder.class)
public class ThrowableProxyLogItem {

    private String className;
    private String message;
    private String stackTrace;
    private ThrowableProxyLogItem cause;
    private List<ThrowableProxyLogItem> suppressed;

    public static ThrowableProxyBuilder getBuilder() {
        return new ThrowableProxyBuilder();
    }

    /**
     * Builder for {@link ThrowableProxyLogItem}.
     */
    public static final class ThrowableProxyBuilder {
        private String className;
        private String message;
        private String stackTrace;
        private ThrowableProxyLogItem cause;
        private List<ThrowableProxyLogItem> suppressed;

        private ThrowableProxyBuilder() {
        }

        public ThrowableProxyBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public ThrowableProxyBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ThrowableProxyBuilder withStackTraceElementProxyArray(List<StackTraceElementProxyLogItem> stackTraceElementProxyArray) {
            this.stackTrace = stackTraceElementProxyArray.stream()
                    .map(StackTraceElementProxyLogItem::getSteasString)
                    .collect(Collectors.joining(System.lineSeparator()));
            return this;
        }

        public ThrowableProxyBuilder withCause(ThrowableProxyLogItem cause) {
            this.cause = cause;
            return this;
        }

        public ThrowableProxyBuilder withSuppressed(List<ThrowableProxyLogItem> suppressed) {
            this.suppressed = suppressed;
            return this;
        }

        public ThrowableProxyBuilder withStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public ThrowableProxyLogItem build() {
            ThrowableProxyLogItem throwableProxy = new ThrowableProxyLogItem();
            throwableProxy.stackTrace = this.stackTrace;
            throwableProxy.cause = this.cause;
            throwableProxy.className = this.className;
            throwableProxy.message = this.message;
            throwableProxy.suppressed = this.suppressed;
            return throwableProxy;
        }
    }
}

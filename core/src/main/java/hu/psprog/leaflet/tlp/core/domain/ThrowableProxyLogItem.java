package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception (throwableProxy) node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = ThrowableProxyLogItem.ThrowableProxyBuilder.class)
public class ThrowableProxyLogItem {

    private String className;
    private String message;
    private String stackTrace;
    private ThrowableProxyLogItem cause;
    private List<ThrowableProxyLogItem> suppressed;

    public String getClassName() {
        return className;
    }

    public String getMessage() {
        return message;
    }

    public ThrowableProxyLogItem getCause() {
        return cause;
    }

    public List<ThrowableProxyLogItem> getSuppressed() {
        return suppressed;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ThrowableProxyLogItem that = (ThrowableProxyLogItem) o;

        return new EqualsBuilder()
                .append(className, that.className)
                .append(message, that.message)
                .append(stackTrace, that.stackTrace)
                .append(cause, that.cause)
                .append(suppressed, that.suppressed)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(className)
                .append(message)
                .append(stackTrace)
                .append(cause)
                .append(suppressed)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("className", className)
                .append("message", message)
                .append("stackTrace", stackTrace)
                .append("cause", cause)
                .append("suppressed", suppressed)
                .toString();
    }

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

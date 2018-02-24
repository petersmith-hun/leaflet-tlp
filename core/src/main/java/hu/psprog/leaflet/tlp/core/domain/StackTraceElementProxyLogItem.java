package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Stacktrace element node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = StackTraceElementProxyLogItem.StackTraceElementProxyBuilder.class)
public class StackTraceElementProxyLogItem {

    private String steasString;

    public String getSteasString() {
        return steasString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StackTraceElementProxyLogItem that = (StackTraceElementProxyLogItem) o;

        return new EqualsBuilder()
                .append(steasString, that.steasString)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(steasString)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("steasString", steasString)
                .toString();
    }

    public static StackTraceElementProxyBuilder getBuilder() {
        return new StackTraceElementProxyBuilder();
    }

    public static final class StackTraceElementProxyBuilder {
        private String steasString;

        private StackTraceElementProxyBuilder() {
        }

        public StackTraceElementProxyBuilder withSteasString(String steasString) {
            this.steasString = steasString;
            return this;
        }

        public StackTraceElementProxyLogItem build() {
            StackTraceElementProxyLogItem stackTraceElementProxy = new StackTraceElementProxyLogItem();
            stackTraceElementProxy.steasString = this.steasString;
            return stackTraceElementProxy;
        }
    }
}

package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

/**
 * Stacktrace element node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@Data
@JsonDeserialize(builder = StackTraceElementProxyLogItem.StackTraceElementProxyBuilder.class)
public class StackTraceElementProxyLogItem {

    private String steasString;

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

package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

/**
 * Log level node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@Data
@JsonDeserialize(builder = LogLevel.LogLevelBuilder.class)
public class LogLevel {

    private String levelStr;

    public static LogLevelBuilder getBuilder() {
        return new LogLevelBuilder();
    }

    /**
     * Builder for {@link LogLevel}.
     */
    public static final class LogLevelBuilder {
        private String levelStr;

        private LogLevelBuilder() {
        }

        public LogLevelBuilder withLevelStr(String levelStr) {
            this.levelStr = levelStr;
            return this;
        }

        public LogLevel build() {
            LogLevel logLevel = new LogLevel();
            logLevel.levelStr = this.levelStr;
            return logLevel;
        }
    }
}

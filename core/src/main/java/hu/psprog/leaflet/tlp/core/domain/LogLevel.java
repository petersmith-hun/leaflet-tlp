package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Log level node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = LogLevel.LogLevelBuilder.class)
public class LogLevel {

    private String levelStr;

    public String getLevelStr() {
        return levelStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LogLevel logLevel = (LogLevel) o;

        return new EqualsBuilder()
                .append(levelStr, logLevel.levelStr)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(levelStr)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("levelStr", levelStr)
                .toString();
    }

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

package hu.psprog.leaflet.tlp.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * Domain object for parsing and storing received log events.
 *
 * @author Peter Smith
 */
@Data
@Document
@JsonDeserialize(builder = LoggingEvent.LoggingEventBuilder.class)
public class LoggingEvent {

    @Id
    private String id;

    private String threadName;
    private String loggerName;

    @Indexed(name = "tlp-index.level")
    private String level;
    private String content;
    private ThrowableProxyLogItem exception;

    @Indexed(name = "tlp-index.timestamp")
    private Date timeStamp;

    @Indexed(name = "tlp-index.source")
    private String source;

    @Indexed(name = "tlp-index.context")
    private Map<String, String> context;

    public static LoggingEventBuilder getBuilder() {
        return new LoggingEventBuilder();
    }

    /**
     * Builder for {@link LoggingEvent}.
     */
    public static final class LoggingEventBuilder {
        private String threadName;
        private String loggerName;
        private String level;
        private String content;
        private ThrowableProxyLogItem exception;
        private Date timeStamp;
        private String source;
        private Map<String, String> context;

        private LoggingEventBuilder() {
        }

        public LoggingEventBuilder withThreadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public LoggingEventBuilder withLoggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public LoggingEventBuilder withLevel(LogLevel level) {
            this.level = level.getLevelStr();
            return this;
        }

        public LoggingEventBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public LoggingEventBuilder withException(ThrowableProxyLogItem exception) {
            this.exception = exception;
            return this;
        }

        public LoggingEventBuilder withFormattedMessage(String formattedMessage) {
            this.content = formattedMessage;
            return this;
        }

        public LoggingEventBuilder withThrowableProxy(ThrowableProxyLogItem throwableProxy) {
            this.exception = throwableProxy;
            return this;
        }

        public LoggingEventBuilder withTimeStamp(long timeStamp) {
            this.timeStamp = new Date(timeStamp);
            return this;
        }

        public LoggingEventBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public LoggingEventBuilder withContext(Map<String, String> context) {
            this.context = context;
            return this;
        }

        public LoggingEvent build() {
            LoggingEvent loggingEvent = new LoggingEvent();
            loggingEvent.content = this.content;
            loggingEvent.loggerName = this.loggerName;
            loggingEvent.exception = this.exception;
            loggingEvent.threadName = this.threadName;
            loggingEvent.timeStamp = this.timeStamp;
            loggingEvent.level = this.level;
            loggingEvent.source = this.source;
            loggingEvent.context = this.context;
            return loggingEvent;
        }
    }
}

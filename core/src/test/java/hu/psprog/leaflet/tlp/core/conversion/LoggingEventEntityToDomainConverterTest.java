package hu.psprog.leaflet.tlp.core.conversion;

import hu.psprog.leaflet.tlp.core.domain.LogLevel;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.domain.ThrowableProxyLogItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link LoggingEventEntityToDomainConverter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class LoggingEventEntityToDomainConverterTest {

    private static final String SOURCE = "source";
    private static final Date TIME_STAMP = new Date();
    private static final String CONTENT = "content";
    private static final LogLevel LOG_LEVEL = LogLevel.getBuilder().withLevelStr("info").build();
    private static final String LOGGER_NAME = "logger-name";
    private static final String THREAD_NAME = "thread-1";

    @InjectMocks
    private LoggingEventEntityToDomainConverter converter;

    @Test
    public void shouldConvertWithoutExceptionAndContext() {

        // given
        LoggingEvent loggingEvent = LoggingEvent.getBuilder()
                .withSource(SOURCE)
                .withTimeStamp(TIME_STAMP.getTime())
                .withContent(CONTENT)
                .withLevel(LOG_LEVEL)
                .withLoggerName(LOGGER_NAME)
                .withThreadName(THREAD_NAME)
                .build();

        // when
        hu.psprog.leaflet.tlp.api.domain.LoggingEvent result = converter.convert(loggingEvent);

        // then
        assertThat(result, notNullValue());
        assertThat(result.source(), equalTo(SOURCE));
        assertThat(result.timeStamp(), equalTo(TIME_STAMP));
        assertThat(result.content(), equalTo(CONTENT));
        assertThat(result.level(), equalTo(LOG_LEVEL.getLevelStr()));
        assertThat(result.loggerName(), equalTo(LOGGER_NAME));
        assertThat(result.threadName(), equalTo(THREAD_NAME));
        assertThat(result.context(), equalTo(Collections.emptyMap()));
        assertThat(result.exception(), nullValue());
    }

    @Test
    public void shouldConvertWithExceptionAndContext() {

        // given
        ThrowableProxyLogItem cause1 = prepareThrowableProxyLogItem(2, null, false);
        ThrowableProxyLogItem cause2 = prepareThrowableProxyLogItem(3, cause1, true);
        Map<String, String> context = Map.of("requestID", "request-1234");
        LoggingEvent loggingEvent = LoggingEvent.getBuilder()
                .withException(prepareThrowableProxyLogItem(1, cause2, false))
                .withContext(context)
                .build();

        // when
        hu.psprog.leaflet.tlp.api.domain.LoggingEvent result = converter.convert(loggingEvent);

        // then
        assertThat(result, notNullValue());
        assertThat(result.exception(), notNullValue());
        assertThat(result.exception().className(), equalTo("class-name 1"));
        assertThat(result.exception().message(), equalTo("exception message 1"));
        assertThat(result.exception().stackTrace(), equalTo("stack-trace 1"));
        assertThat(result.exception().suppressed().isEmpty(), is(true));
        assertThat(result.exception().cause().className(), equalTo("class-name 3"));
        assertThat(result.exception().cause().message(), equalTo("exception message 3"));
        assertThat(result.exception().cause().stackTrace(), equalTo("stack-trace 3"));
        assertThat(result.exception().cause().suppressed().size(), equalTo(1));
        assertThat(result.exception().cause().suppressed().get(0), equalTo(result.exception().cause().cause()));
        assertThat(result.exception().cause().cause().className(), equalTo("class-name 2"));
        assertThat(result.exception().cause().cause().message(), equalTo("exception message 2"));
        assertThat(result.exception().cause().cause().stackTrace(), equalTo("stack-trace 2"));
        assertThat(result.exception().cause().cause().cause(), nullValue());
        assertThat(result.exception().cause().cause().suppressed().isEmpty(), is(true));
        assertThat(result.context(), equalTo(context));
    }

    private ThrowableProxyLogItem prepareThrowableProxyLogItem(int exceptionID, ThrowableProxyLogItem cause, boolean withSuppressed) {
        return ThrowableProxyLogItem.getBuilder()
                .withClassName("class-name " + exceptionID)
                .withMessage("exception message " + exceptionID)
                .withStackTrace("stack-trace " + exceptionID)
                .withCause(cause)
                .withSuppressed(withSuppressed
                        ? Collections.singletonList(cause)
                        : null)
                .build();
    }
}
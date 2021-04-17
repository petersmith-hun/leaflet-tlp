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
    public void shouldConvertWithoutException() {

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
        assertThat(result.getSource(), equalTo(SOURCE));
        assertThat(result.getTimeStamp(), equalTo(TIME_STAMP));
        assertThat(result.getContent(), equalTo(CONTENT));
        assertThat(result.getLevel(), equalTo(LOG_LEVEL.getLevelStr()));
        assertThat(result.getLoggerName(), equalTo(LOGGER_NAME));
        assertThat(result.getThreadName(), equalTo(THREAD_NAME));
        assertThat(result.getException(), nullValue());
    }

    @Test
    public void shouldConvertWithException() {

        // given
        ThrowableProxyLogItem cause1 = prepareThrowableProxyLogItem(2, null, false);
        ThrowableProxyLogItem cause2 = prepareThrowableProxyLogItem(3, cause1, true);
        LoggingEvent loggingEvent = LoggingEvent.getBuilder()
                .withException(prepareThrowableProxyLogItem(1, cause2, false))
                .build();

        // when
        hu.psprog.leaflet.tlp.api.domain.LoggingEvent result = converter.convert(loggingEvent);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getException(), notNullValue());
        assertThat(result.getException().getClassName(), equalTo("class-name 1"));
        assertThat(result.getException().getMessage(), equalTo("exception message 1"));
        assertThat(result.getException().getStackTrace(), equalTo("stack-trace 1"));
        assertThat(result.getException().getSuppressed().isEmpty(), is(true));
        assertThat(result.getException().getCause().getClassName(), equalTo("class-name 3"));
        assertThat(result.getException().getCause().getMessage(), equalTo("exception message 3"));
        assertThat(result.getException().getCause().getStackTrace(), equalTo("stack-trace 3"));
        assertThat(result.getException().getCause().getSuppressed().size(), equalTo(1));
        assertThat(result.getException().getCause().getSuppressed().get(0), equalTo(result.getException().getCause().getCause()));
        assertThat(result.getException().getCause().getCause().getClassName(), equalTo("class-name 2"));
        assertThat(result.getException().getCause().getCause().getMessage(), equalTo("exception message 2"));
        assertThat(result.getException().getCause().getCause().getStackTrace(), equalTo("stack-trace 2"));
        assertThat(result.getException().getCause().getCause().getCause(), nullValue());
        assertThat(result.getException().getCause().getCause().getSuppressed().isEmpty(), is(true));
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
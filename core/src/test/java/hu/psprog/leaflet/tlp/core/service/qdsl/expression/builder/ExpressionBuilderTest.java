package hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.QLoggingEvent;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.strategy.ExpressionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link ExpressionBuilder}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ExpressionBuilderTest {

    private static final LogRequest LOG_REQUEST = new LogRequest();
    private static final QLoggingEvent Q_LOGGING_EVENT = new QLoggingEvent("event");
    private static final BooleanExpression CONTENT_EXPRESSION = Q_LOGGING_EVENT.content.eq("test-content");
    private static final BooleanExpression SOURCE_EXPRESSION = Q_LOGGING_EVENT.source.eq("test-source");

    @Mock(lenient = true)
    private ExpressionStrategy applicableStrategy;

    @Mock(lenient = true)
    private ExpressionStrategy otherApplicableStrategy;

    @Mock(lenient = true)
    private ExpressionStrategy nonApplicableStrategy;

    private ExpressionBuilder expressionBuilder;

    @BeforeEach
    public void setup() {
        given(applicableStrategy.applyStrategy(any(), any())).willReturn(Optional.of(CONTENT_EXPRESSION));
        given(otherApplicableStrategy.applyStrategy(any(), any())).willReturn(Optional.of(SOURCE_EXPRESSION));
        given(nonApplicableStrategy.applyStrategy(any(), any())).willReturn(Optional.empty());
    }

    @Test
    public void shouldBuildSingleExpression() {

        // given
        expressionBuilder = new ExpressionBuilder(Arrays.asList(applicableStrategy, nonApplicableStrategy));

        // when
        Optional<Predicate> result = expressionBuilder.build(LOG_REQUEST);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(CONTENT_EXPRESSION));
    }

    @Test
    public void shouldBuildExpressionChain() {

        // given
        expressionBuilder = new ExpressionBuilder(Arrays.asList(applicableStrategy, nonApplicableStrategy, otherApplicableStrategy));
        Predicate expectedPredicate = new BooleanBuilder()
                .and(CONTENT_EXPRESSION)
                .and(SOURCE_EXPRESSION)
                .getValue();

        // when
        Optional<Predicate> result = expressionBuilder.build(LOG_REQUEST);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(expectedPredicate));
    }

    @Test
    public void shouldBuildEmptyExpression() {

        // given
        expressionBuilder = new ExpressionBuilder(Collections.singletonList(nonApplicableStrategy));

        // when
        Optional<Predicate> result = expressionBuilder.build(LOG_REQUEST);

        // then
        assertThat(result.isPresent(), is(false));
    }
}
package hu.psprog.leaflet.tlp.core.persistence.dao.impl;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.persistence.repository.LogEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link LogEventDAOImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class LogEventDAOImplTest {

    private static final LoggingEvent LOGGING_EVENT = LoggingEvent.getBuilder().build();

    @Mock
    private LogEventRepository logEventRepository;

    @Mock
    private Pageable pageable;

    @Mock
    private Predicate predicate;

    @InjectMocks
    private LogEventDAOImpl logEventDAO;

    @Test
    public void shouldFindAllWithPage() {

        // when
        logEventDAO.findAll(pageable);

        // then
        verify(logEventRepository).findAll(pageable);
    }

    @Test
    public void shouldFindAllWithPageAndPredicate() {

        // when
        logEventDAO.findAll(predicate, pageable);

        // then
        verify(logEventRepository).findAll(predicate, pageable);
    }

    @Test
    public void shouldSave() {

        // when
        logEventDAO.save(LOGGING_EVENT);

        // then
        verify(logEventRepository).save(LOGGING_EVENT);
    }
}
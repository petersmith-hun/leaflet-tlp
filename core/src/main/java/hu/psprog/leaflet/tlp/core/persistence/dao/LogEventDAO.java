package hu.psprog.leaflet.tlp.core.persistence.dao;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * {@link LoggingEvent} DAO operations.
 *
 * @author Peter Smith
 */
public interface LogEventDAO {

    /**
     * Returns paged list of {@link LoggingEvent} entries that conforms given paging parameters.
     *
     * @param pageable paging parameters
     * @return paged list of {@link LoggingEvent} entries
     */
    Page<LoggingEvent> findAll(Pageable pageable);

    /**
     * Returns paged list of {@link LoggingEvent} entries that conforms given paging parameters and filters expressions.
     *
     * @param predicate QueryDSL filter expression
     * @param pageable paging parameters
     * @return paged list of {@link LoggingEvent} entries
     */
    Page<LoggingEvent> findAll(Predicate predicate, Pageable pageable);

    /**
     * Stores given {@link LoggingEvent} object.
     *
     * @param loggingEvent {@link LoggingEvent} object to store
     */
    void save(LoggingEvent loggingEvent);
}

package hu.psprog.leaflet.tlp.core.persistence.repository;

import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * {@link LoggingEvent} Mongo repository interface.
 *
 * @author Peter Smith
 */
@Repository
public interface LogEventRepository extends MongoRepository<LoggingEvent, String>, QueryDslPredicateExecutor<LoggingEvent> {
}

package hu.psprog.leaflet.tlp.core.persistence.dao.impl;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.persistence.dao.LogEventDAO;
import hu.psprog.leaflet.tlp.core.persistence.repository.LogEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link LogEventDAO}.
 *
 * @author Peter Smith
 */
@Component
public class LogEventDAOImpl implements LogEventDAO {

    private LogEventRepository logEventRepository;

    @Autowired
    public LogEventDAOImpl(LogEventRepository logEventRepository) {
        this.logEventRepository = logEventRepository;
    }

    @Override
    public Page<LoggingEvent> findAll(Pageable pageable) {
        return logEventRepository.findAll(pageable);
    }

    @Override
    public Page<LoggingEvent> findAll(Predicate predicate, Pageable pageable) {
        return logEventRepository.findAll(predicate, pageable);
    }

    @Override
    public void save(LoggingEvent loggingEvent) {
        logEventRepository.save(loggingEvent);
    }
}

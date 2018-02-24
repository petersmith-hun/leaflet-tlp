package hu.psprog.leaflet.tlp.core.service.impl;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.core.domain.LogEventPage;
import hu.psprog.leaflet.tlp.core.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.persistence.dao.LogEventDAO;
import hu.psprog.leaflet.tlp.core.service.LogProcessingService;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder.ExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link LogProcessingService}.
 *
 * @author Peter Smith
 */
@Service
public class LogProcessingServiceImpl implements LogProcessingService {

    private LogEventDAO logEventDAO;
    private ConversionService conversionService;
    private ExpressionBuilder expressionBuilder;

    @Autowired
    public LogProcessingServiceImpl(LogEventDAO logEventDAO, ConversionService conversionService, ExpressionBuilder expressionBuilder) {
        this.logEventDAO = logEventDAO;
        this.conversionService = conversionService;
        this.expressionBuilder = expressionBuilder;
    }

    @Override
    public LogEventPage getLogs(LogRequest logRequest) {

        Optional<Predicate> expression = expressionBuilder.build(logRequest);
        Pageable pageable = conversionService.convert(logRequest, Pageable.class);
        Page<LoggingEvent> loggingEventPage;
        if (expression.isPresent()) {
            loggingEventPage = logEventDAO.findAll(expression.get(), pageable);
        } else {
            loggingEventPage = logEventDAO.findAll(pageable);
        }

        return conversionService.convert(loggingEventPage, LogEventPage.class);
    }

    @Override
    public void storeLog(LoggingEvent loggingEvent) {
        logEventDAO.save(loggingEvent);
    }
}

package hu.psprog.leaflet.tlp.core.service.impl;

import com.querydsl.core.types.Predicate;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.core.domain.LoggingEvent;
import hu.psprog.leaflet.tlp.core.persistence.dao.LogEventDAO;
import hu.psprog.leaflet.tlp.core.service.LogProcessingService;
import hu.psprog.leaflet.tlp.core.service.qdsl.expression.builder.ExpressionBuilder;
import hu.psprog.leaflet.tlql.ir.DSLQueryModel;
import hu.psprog.leaflet.tlql.processor.TLQLProcessorService;
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

    private final LogEventDAO logEventDAO;
    private final ConversionService conversionService;
    private final ExpressionBuilder expressionBuilder;
    private final TLQLProcessorService tlqlProcessorService;

    @Autowired
    public LogProcessingServiceImpl(LogEventDAO logEventDAO, ConversionService conversionService,
                                    ExpressionBuilder expressionBuilder, TLQLProcessorService tlqlProcessorService) {
        this.logEventDAO = logEventDAO;
        this.conversionService = conversionService;
        this.expressionBuilder = expressionBuilder;
        this.tlqlProcessorService = tlqlProcessorService;
    }

    @Override
    public LogEventPage getLogs(LogRequest logRequest) {

        return retrieveLogs(conversionService.convert(logRequest, DSLQueryModel.class));
    }

    @Override
    public LogEventPage getLogs(String logRequest) {

        return retrieveLogs(tlqlProcessorService.parse(logRequest));
    }

    @Override
    public void storeLog(LoggingEvent loggingEvent) {
        logEventDAO.save(loggingEvent);
    }

    private LogEventPage retrieveLogs(DSLQueryModel dslQueryModel) {

        Optional<Predicate> expression = expressionBuilder.build(dslQueryModel);
        Pageable pageable = conversionService.convert(dslQueryModel, Pageable.class);

        Page<LoggingEvent> loggingEventPage;
        if (expression.isPresent()) {
            loggingEventPage = logEventDAO.findAll(expression.get(), pageable);
        } else {
            loggingEventPage = logEventDAO.findAll(pageable);
        }

        return conversionService.convert(loggingEventPage, LogEventPage.class);
    }
}

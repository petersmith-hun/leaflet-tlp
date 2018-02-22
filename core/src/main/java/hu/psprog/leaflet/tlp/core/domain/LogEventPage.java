package hu.psprog.leaflet.tlp.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * List of {@link LoggingEvent} objects with paging information.
 *
 * @author Peter Smith
 */
public class LogEventPage {

    private long entityCount;
    private int pageCount;
    private int pageNumber;
    private int pageSize;
    private int entityCountOnPage;
    private List<LoggingEvent> entitiesOnPage;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public long getEntityCount() {
        return entityCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getEntityCountOnPage() {
        return entityCountOnPage;
    }

    public List<LoggingEvent> getEntitiesOnPage() {
        return entitiesOnPage;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LogEventPage that = (LogEventPage) o;

        return new EqualsBuilder()
                .append(entityCount, that.entityCount)
                .append(pageCount, that.pageCount)
                .append(pageNumber, that.pageNumber)
                .append(pageSize, that.pageSize)
                .append(entityCountOnPage, that.entityCountOnPage)
                .append(first, that.first)
                .append(last, that.last)
                .append(hasNext, that.hasNext)
                .append(hasPrevious, that.hasPrevious)
                .append(entitiesOnPage, that.entitiesOnPage)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(entityCount)
                .append(pageCount)
                .append(pageNumber)
                .append(pageSize)
                .append(entityCountOnPage)
                .append(entitiesOnPage)
                .append(first)
                .append(last)
                .append(hasNext)
                .append(hasPrevious)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("entityCount", entityCount)
                .append("pageCount", pageCount)
                .append("pageNumber", pageNumber)
                .append("pageSize", pageSize)
                .append("entityCountOnPage", entityCountOnPage)
                .append("entitiesOnPage", entitiesOnPage)
                .append("first", first)
                .append("last", last)
                .append("hasNext", hasNext)
                .append("hasPrevious", hasPrevious)
                .toString();
    }

    public static LogEventPageBuilder getBuilder() {
        return new LogEventPageBuilder();
    }

    /**
     * Builder for {@link LogEventPage}.
     */
    public static final class LogEventPageBuilder {
        private long entityCount;
        private int pageCount;
        private int pageNumber;
        private int pageSize;
        private int entityCountOnPage;
        private List<LoggingEvent> entitiesOnPage;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;

        private LogEventPageBuilder() {
        }

        public LogEventPageBuilder withEntityCount(long entityCount) {
            this.entityCount = entityCount;
            return this;
        }

        public LogEventPageBuilder withPageCount(int pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public LogEventPageBuilder withPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public LogEventPageBuilder withPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public LogEventPageBuilder withEntityCountOnPage(int entityCountOnPage) {
            this.entityCountOnPage = entityCountOnPage;
            return this;
        }

        public LogEventPageBuilder withEntitiesOnPage(List<LoggingEvent> entitiesOnPage) {
            this.entitiesOnPage = entitiesOnPage;
            return this;
        }

        public LogEventPageBuilder withFirst(boolean first) {
            this.first = first;
            return this;
        }

        public LogEventPageBuilder withLast(boolean last) {
            this.last = last;
            return this;
        }

        public LogEventPageBuilder withHasNext(boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public LogEventPageBuilder withHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public LogEventPage build() {
            LogEventPage logEventPage = new LogEventPage();
            logEventPage.pageNumber = this.pageNumber;
            logEventPage.pageCount = this.pageCount;
            logEventPage.pageSize = this.pageSize;
            logEventPage.hasNext = this.hasNext;
            logEventPage.entityCount = this.entityCount;
            logEventPage.entitiesOnPage = this.entitiesOnPage;
            logEventPage.last = this.last;
            logEventPage.first = this.first;
            logEventPage.hasPrevious = this.hasPrevious;
            logEventPage.entityCountOnPage = this.entityCountOnPage;
            return logEventPage;
        }
    }
}

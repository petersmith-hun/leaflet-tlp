package hu.psprog.leaflet.tlp.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Objects;

/**
 * Log retrieval request object.
 *
 * @author Peter Smith
 */
public class LogRequest {

    private String source;
    private String level;
    private Date from;
    private Date to;
    private String content;
    private int page;
    private int limit;
    private OrderBy orderBy;
    private OrderDirection orderDirection;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPage() {

        if (page == 0) {
            page = 1;
        }

        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {

        if (limit == 0) {
            limit = 50;
        }

        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public OrderBy getOrderBy() {

        if (Objects.isNull(orderBy)) {
            orderBy = OrderBy.TIMESTAMP;
        }

        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public OrderDirection getOrderDirection() {

        if (Objects.isNull(orderDirection)) {
            orderDirection = OrderDirection.DESC;
        }

        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LogRequest that = (LogRequest) o;

        return new EqualsBuilder()
                .append(page, that.page)
                .append(limit, that.limit)
                .append(source, that.source)
                .append(level, that.level)
                .append(from, that.from)
                .append(to, that.to)
                .append(content, that.content)
                .append(orderBy, that.orderBy)
                .append(orderDirection, that.orderDirection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(source)
                .append(level)
                .append(from)
                .append(to)
                .append(content)
                .append(page)
                .append(limit)
                .append(orderBy)
                .append(orderDirection)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("source", source)
                .append("level", level)
                .append("from", from)
                .append("to", to)
                .append("content", content)
                .append("page", page)
                .append("limit", limit)
                .append("orderBy", orderBy)
                .append("orderDirection", orderDirection)
                .toString();
    }
}

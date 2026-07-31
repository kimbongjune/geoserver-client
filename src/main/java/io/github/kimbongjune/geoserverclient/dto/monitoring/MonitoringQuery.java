package io.github.kimbongjune.geoserverclient.dto.monitoring;
import java.util.Objects;

/**
 * Query parameters for {@code GET /rest/monitor/requests}.
 *
 * <p>All fields are optional (nullable). Unset fields are omitted from the request.
 *
 * <p>Example:
 * <pre>
 * MonitoringQuery query = new MonitoringQuery()
 *     .count(5)
 *     .filter("status:EQ:FINISHED")
 *     .order("startTime;DESC");
 * List&lt;MonitorRequestSummary&gt; list = monitoring.list(query);
 * </pre>
 */
public class MonitoringQuery {

    /** Start date/time filter. Example: {@code 2026-03-23T00:00:00} */
    private String from;

    /** End date/time filter. Same format as {@code from}. */
    private String to;

    /**
     * Field filter. Format: {@code {field}:{COMPARISON}:{value}}.
     * <ul>
     *   <li>Example: {@code status:EQ:FINISHED}</li>
     *   <li>Example: {@code status:IN:FINISHED,FAILED}</li>
     *   <li>Example: {@code httpMethod:EQ:GET}</li>
     * </ul>
     */
    private String filter;

    /**
     * Sort order. Format: {@code {field};{ASC|DESC}}.
     * <ul>
     *   <li>Example: {@code startTime;DESC}</li>
     *   <li>Example: {@code startTime;ASC}</li>
     * </ul>
     */
    private String order;

    /** Number of records to skip. */
    private Long offset;

    /** Maximum number of records to return. */
    private Long count;

    /**
     * Filter by live status.
     * <ul>
     *   <li>{@code true} — only RUNNING/WAITING/CANCELLING requests</li>
     *   <li>{@code false} — only FINISHED/FAILED requests</li>
     *   <li>{@code null} — no filter (default)</li>
     * </ul>
     */
    private Boolean live;

    /** Constructs an empty {@code MonitoringQuery} with no filters set. */
    public MonitoringQuery() {}

    // Fluent setters

    /**
     * Sets the start date/time filter.
     * @param from the start date/time (e.g. {@code "2026-03-23T00:00:00"})
     * @return this instance for chaining
     */
    public MonitoringQuery from(String from) {
        this.from = from;     return this;
    }

    /**
     * Sets the end date/time filter.
     * @param to the end date/time
     * @return this instance for chaining
     */
    public MonitoringQuery to(String to) {
        this.to = to;         return this;
    }

    /**
     * Sets the field filter expression.
     * @param filter the filter expression (e.g. {@code "status:EQ:FINISHED"})
     * @return this instance for chaining
     */
    public MonitoringQuery filter(String filter) {
        this.filter = filter; return this;
    }

    /**
     * Sets the sort order expression.
     * @param order the sort order (e.g. {@code "startTime;DESC"})
     * @return this instance for chaining
     */
    public MonitoringQuery order(String order) {
        this.order = order;   return this;
    }

    /**
     * Sets the number of records to skip.
     * @param offset the offset
     * @return this instance for chaining
     */
    public MonitoringQuery offset(long offset) {
        this.offset = offset; return this;
    }

    /**
     * Sets the maximum number of records to return.
     * @param count the record count limit
     * @return this instance for chaining
     */
    public MonitoringQuery count(long count) {
        this.count = count;   return this;
    }

    /**
     * Sets the live filter.
     * @param live {@code true} for live only, {@code false} for finished only
     * @return this instance for chaining
     */
    public MonitoringQuery live(boolean live) {
        this.live = live;     return this;
    }

    // Getters

    /** @return the start date/time filter */
    public String  getFrom() {
        return from;
    }
    /** @return the end date/time filter */
    public String  getTo() {
        return to;
    }
    /** @return the field filter expression */
    public String  getFilter() {
        return filter;
    }
    /** @return the sort order expression */
    public String  getOrder() {
        return order;
    }
    /** @return the offset (number of records to skip) */
    public Long    getOffset() {
        return offset;
    }
    /** @return the maximum number of records to return */
    public Long    getCount() {
        return count;
    }
    /** @return the live filter flag */
    public Boolean getLive() {
        return live;
    }

    /**
     * Returns {@code true} when no query parameters have been set.
     * @return {@code true} if all parameters are null
     */
    public boolean isEmpty() {
        return from == null && to == null && filter == null
                && order == null && offset == null && count == null && live == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonitoringQuery that = (MonitoringQuery) o;
        return Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && Objects.equals(filter, that.filter)
                && Objects.equals(order, that.order)
                && Objects.equals(offset, that.offset)
                && Objects.equals(count, that.count)
                && Objects.equals(live, that.live);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, filter, order, offset, count, live);
    }

    @Override
    public String toString() {
        return "MonitoringQuery{" +
                "from=" + from +
                ", to=" + to +
                ", filter=" + filter +
                ", order=" + order +
                ", offset=" + offset +
                ", count=" + count +
                ", live=" + live +
                '}';
    }
}

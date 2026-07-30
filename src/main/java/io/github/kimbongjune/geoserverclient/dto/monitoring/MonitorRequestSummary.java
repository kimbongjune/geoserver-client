package io.github.kimbongjune.geoserverclient.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * Summary entry returned when listing monitoring requests.
 *
 * <p>Maps each item in the array returned by {@code GET /rest/monitor/requests}.
 * Note: the {@code name} field actually contains the request ID (a long integer).</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitorRequestSummary {

    private long name;  // actually the request ID (integer)
    private String href;

    public MonitorRequestSummary() {}

    public long getName() { return name; }
    public String getHref() { return href; }

    /** Convenience alias — returns {@code name}, which is the request ID. */
    public long getId() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitorRequestSummary that = (MonitorRequestSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "MonitorRequestSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

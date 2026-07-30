package io.github.kimbongjune.geoserverclient.dto.wmsstore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing WMS stores (name + href only).
 *
 * <p>Maps each item in the array returned by {@code GET /rest/workspaces/{ws}/wmsstores}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WmsStoreSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public WmsStoreSummary() {}

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WmsStoreSummary that = (WmsStoreSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "WmsStoreSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

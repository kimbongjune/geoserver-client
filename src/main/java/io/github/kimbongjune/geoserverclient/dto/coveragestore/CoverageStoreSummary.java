package io.github.kimbongjune.geoserverclient.dto.coveragestore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing coverage stores (name + href only).
 *
 * <p>Maps each item in the array returned by {@code GET /rest/workspaces/{ws}/coveragestores}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverageStoreSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public CoverageStoreSummary() {}

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoverageStoreSummary that = (CoverageStoreSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "CoverageStoreSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

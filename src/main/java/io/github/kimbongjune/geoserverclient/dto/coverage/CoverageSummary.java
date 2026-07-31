package io.github.kimbongjune.geoserverclient.dto.coverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing coverages (name + href only).
 *
 * <p>Maps each item in the array returned by {@code GET /rest/workspaces/{ws}/coveragestores/{cs}/coverages}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverageSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    /** Constructs an empty {@code CoverageSummary} for deserialization. */
    public CoverageSummary() {}

    /** @return the coverage name */
    public String getName() {
        return name;
    }
    /** @return the href to the full coverage resource */
    public String getHref() {
        return href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoverageSummary that = (CoverageSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "CoverageSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

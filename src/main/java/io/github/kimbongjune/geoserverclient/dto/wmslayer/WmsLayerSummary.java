package io.github.kimbongjune.geoserverclient.dto.wmslayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing WMS layers (name + href only).
 *
 * <p>Maps each item in the array returned by
 * {@code GET /rest/workspaces/{ws}/wmsstores/{store}/wmslayers}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WmsLayerSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public WmsLayerSummary() {}

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WmsLayerSummary that = (WmsLayerSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "WmsLayerSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

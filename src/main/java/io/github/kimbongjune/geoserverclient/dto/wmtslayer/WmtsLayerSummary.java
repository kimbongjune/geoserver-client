package io.github.kimbongjune.geoserverclient.dto.wmtslayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing WMTS layers (name + href only).
 *
 * <p>Maps each item in the array returned by
 * {@code GET /rest/workspaces/{ws}/wmtsstores/{store}/layers}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WmtsLayerSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public WmtsLayerSummary() {}

    public String getName() {
        return name;
    }
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
        WmtsLayerSummary that = (WmtsLayerSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "WmtsLayerSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

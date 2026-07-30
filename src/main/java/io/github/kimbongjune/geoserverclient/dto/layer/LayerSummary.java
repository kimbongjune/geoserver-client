package io.github.kimbongjune.geoserverclient.dto.layer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing layers (name + href only).
 *
 * <p>Maps each item in the array returned by {@code GET /rest/layers} or
 * {@code GET /rest/workspaces/{ws}/layers}.</p>
 *
 * <p>In a global listing the name takes the form "ws:layerName"; in a workspace-scoped
 * listing the workspace prefix is omitted.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public LayerSummary() {}

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayerSummary that = (LayerSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "LayerSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

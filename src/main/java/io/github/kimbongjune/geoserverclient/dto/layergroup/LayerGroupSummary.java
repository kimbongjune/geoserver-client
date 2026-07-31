package io.github.kimbongjune.geoserverclient.dto.layergroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing layer groups (name + href only).
 *
 * <pre>{"name": "my_group", "href": "http://.../rest/layergroups/my_group.json"}</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerGroupSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    /** Constructs an empty {@code LayerGroupSummary} for deserialization. */
    public LayerGroupSummary() {}

    /** @return the layer group name */
    public String getName() {
        return name;
    }
    /** @return the href to the layer group detail resource */
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
        LayerGroupSummary that = (LayerGroupSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "LayerGroupSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

package io.github.kimbongjune.geoserverclient.dto.style;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing styles (name + href only).
 *
 * <pre>{"name": "raster", "href": "http://.../rest/styles/raster.json"}</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StyleSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public StyleSummary() {}

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleSummary that = (StyleSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "StyleSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

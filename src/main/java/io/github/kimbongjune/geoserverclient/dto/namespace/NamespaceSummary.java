package io.github.kimbongjune.geoserverclient.dto.namespace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing namespaces (name + href only).
 *
 * <p>Maps each item in the array returned by {@code GET /rest/namespaces}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NamespaceSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    /** Constructs an empty {@code NamespaceSummary} for deserialization. */
    public NamespaceSummary() {}

    /** @return the namespace prefix (equals the workspace name) */
    public String getName() {
        return name;
    }
    /** @return the href to the namespace detail resource */
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
        NamespaceSummary that = (NamespaceSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "NamespaceSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

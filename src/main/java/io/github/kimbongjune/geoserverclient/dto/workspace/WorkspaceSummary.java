package io.github.kimbongjune.geoserverclient.dto.workspace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary representation of a workspace as returned in the list response.
 * Contains only name and href (self-link).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkspaceSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    public WorkspaceSummary() {}

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkspaceSummary that = (WorkspaceSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "WorkspaceSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

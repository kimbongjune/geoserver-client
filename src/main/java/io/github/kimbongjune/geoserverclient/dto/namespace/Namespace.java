package io.github.kimbongjune.geoserverclient.dto.namespace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for namespace details.
 *
 * <p>The prefix cannot be changed after creation (PUT returns 403). Only {@code uri} and
 * {@code isolated} are mutable.</p>
 * <p>Namespaces have a 1:1 relationship with workspaces — the prefix equals the workspace name.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Namespace {

    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("isolated")
    private Boolean isolated;

    public Namespace() {}

    public String getPrefix()    { return prefix; }
    public String getUri()       { return uri; }
    public Boolean getIsolated() { return isolated; }
    public boolean isIsolated()  { return Boolean.TRUE.equals(isolated); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Namespace that = (Namespace) o;
        return Objects.equals(prefix, that.prefix)
                && Objects.equals(uri, that.uri)
                && Objects.equals(isolated, that.isolated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, uri, isolated);
    }

    @Override
    public String toString() {
        return "Namespace{" +
                "prefix=" + prefix +
                ", uri=" + uri +
                ", isolated=" + isolated +
                '}';
    }
}

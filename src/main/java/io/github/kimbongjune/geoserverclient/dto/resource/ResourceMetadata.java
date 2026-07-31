package io.github.kimbongjune.geoserverclient.dto.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for resource metadata returned by {@code GET /rest/resource/{path}?operation=metadata&format=json}.
 * <p>
 * Works correctly for directories. For files, GeoServer returns XML instead of JSON.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceMetadata {

    private String name;
    private String lastModified;
    /** {@code "directory"} or {@code "resource"} */
    private String type;

    /** @return the resource name */
    public String getName() {
        return name;
    }
    /** @return the last-modified timestamp as an ISO-8601 string */
    public String getLastModified() {
        return lastModified;
    }
    /** @return the resource type ({@code "directory"} or {@code "resource"}) */
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceMetadata that = (ResourceMetadata) o;
        return Objects.equals(name, that.name)
                && Objects.equals(lastModified, that.lastModified)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastModified, type);
    }

    @Override
    public String toString() {
        return "ResourceMetadata{" +
                "name=" + name +
                ", lastModified=" + lastModified +
                ", type=" + type +
                '}';
    }
}

package io.github.kimbongjune.geoserverclient.dto.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a single child entry in a resource directory listing.
 * <p>Each child has a {@code name} and a {@code link} with href, rel, type.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceChild {

    private String name;
    private ResourceLink link;

    /** @return the child resource name */
    public String getName() {
        return name;
    }
    /** @return the link to the child resource */
    public ResourceLink getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceChild that = (ResourceChild) o;
        return Objects.equals(name, that.name)
                && Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }

    @Override
    public String toString() {
        return "ResourceChild{" +
                "name=" + name +
                ", link=" + link +
                '}';
    }
}

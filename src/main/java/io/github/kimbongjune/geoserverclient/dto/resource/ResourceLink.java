package io.github.kimbongjune.geoserverclient.dto.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a link object inside a resource entry.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceLink {

    private String href;
    private String rel;
    private String type;

    /** @return the link URL */
    public String getHref() {
        return href;
    }
    /** @return the link relation type (e.g. {@code "alternate"}) */
    public String getRel() {
        return rel;
    }
    /** @return the link MIME type */
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
        ResourceLink that = (ResourceLink) o;
        return Objects.equals(href, that.href)
                && Objects.equals(rel, that.rel)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, rel, type);
    }

    @Override
    public String toString() {
        return "ResourceLink{" +
                "href=" + href +
                ", rel=" + rel +
                ", type=" + type +
                '}';
    }
}

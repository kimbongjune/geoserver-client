package io.github.kimbongjune.geoserverclient.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a single template entry in a template list response.
 * <p>
 * The {@code name} always includes the {@code .ftl} extension.
 * The {@code href} uses a {@code .ftl.json} double extension (GeoServer quirk).
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateInfo {

    private String name;
    private String href;

    public String getName() { return name; }
    public String getHref() { return href; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateInfo that = (TemplateInfo) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "TemplateInfo{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

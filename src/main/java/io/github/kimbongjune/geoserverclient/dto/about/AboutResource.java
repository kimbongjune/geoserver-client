package io.github.kimbongjune.geoserverclient.dto.about;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a single resource entry in {@code GET /rest/about/version} or
 * {@code GET /rest/about/manifest} responses.
 *
 * <p>The {@code @name} field identifies the JAR or component name.
 * All other manifest headers are captured in {@code properties} via {@code @JsonAnySetter}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AboutResource {

    @JsonProperty("@name")
    private String name;

    private final Map<String, Object> properties = new LinkedHashMap<String, Object>();

    /**
     * Sets an arbitrary manifest property captured by Jackson's {@code @JsonAnySetter}.
     *
     * @param key   the manifest header name
     * @param value the header value
     */
    @JsonAnySetter
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * Returns an unmodifiable view of all extra manifest properties.
     *
     * @return unmodifiable map of property name to value
     */
    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Returns the {@code @name} attribute identifying the JAR or component.
     *
     * @return the resource name, or {@code null} if not set
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AboutResource that = (AboutResource) o;
        return Objects.equals(name, that.name)
                && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }

    @Override
    public String toString() {
        return "AboutResource{" +
                "name=" + name +
                ", properties=" + properties +
                '}';
    }
}

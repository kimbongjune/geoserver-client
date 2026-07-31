package io.github.kimbongjune.geoserverclient.dto.featuretype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing feature types (name + href only).
 *
 * <p>Maps each item in the {@code featureType} array returned by
 * {@code GET /rest/workspaces/{ws}/datastores/{ds}/featuretypes?list=configured}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureTypeSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    /** Constructs an empty {@code FeatureTypeSummary} for deserialization. */
    public FeatureTypeSummary() {}

    /** @return the feature type name */
    public String getName() {
        return name;
    }
    /** @return the href to the full feature type resource */
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
        FeatureTypeSummary that = (FeatureTypeSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "FeatureTypeSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}

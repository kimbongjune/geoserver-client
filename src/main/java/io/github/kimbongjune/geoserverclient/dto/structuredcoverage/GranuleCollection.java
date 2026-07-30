package io.github.kimbongjune.geoserverclient.dto.structuredcoverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a structured coverage granule list (GeoJSON FeatureCollection).
 *
 * <p>Maps the response of {@code GET /rest/.../index/granules}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GranuleCollection {

    @JsonProperty("type")
    private String type;

    @JsonProperty("features")
    private List<Granule> features;

    public String       getType()     { return type; }
    public List<Granule> getFeatures() {
        return features != null
                ? Collections.unmodifiableList(features)
                : Collections.<Granule>emptyList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Granule {
        @JsonProperty("type")
        private String type;

        @JsonProperty("id")
        private String id;

        @JsonProperty("geometry")
        private Object geometry;

        @JsonProperty("properties")
        private Map<String, Object> properties;

        public String              getId()         { return id; }
        public String              getType()       { return type; }
        public Object              getGeometry()   { return geometry; }
        public Map<String, Object> getProperties() { return properties == null ? null : Collections.unmodifiableMap(properties); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Granule that = (Granule) o;
            return Objects.equals(type, that.type)
                    && Objects.equals(id, that.id)
                    && Objects.equals(geometry, that.geometry)
                    && Objects.equals(properties, that.properties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, id, geometry, properties);
        }

        @Override
        public String toString() {
            return "Granule{" +
                    "type=" + type +
                    ", id=" + id +
                    ", geometry=" + geometry +
                    ", properties=" + properties +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GranuleCollection that = (GranuleCollection) o;
        return Objects.equals(type, that.type)
                && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, features);
    }

    @Override
    public String toString() {
        return "GranuleCollection{" +
                "type=" + type +
                ", features=" + features +
                '}';
    }
}

package io.github.kimbongjune.geoserverclient.dto.structuredcoverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for the index schema of a structured coverage.
 *
 * <p>Maps the response of
 * {@code GET /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{cov}/index}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexSchema {

    @JsonProperty("attributes")
    private Attributes attributes;

    @JsonProperty("href")
    private String href;

    public Attributes getAttributes() { return attributes; }
    public String getHref()           { return href; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {
        @JsonProperty("Attribute")
        private List<SchemaAttribute> attribute;

        public List<SchemaAttribute> getAttribute() { return attribute == null ? null : Collections.unmodifiableList(attribute); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Attributes that = (Attributes) o;
            return Objects.equals(attribute, that.attribute);
        }

        @Override
        public int hashCode() {
            return Objects.hash(attribute);
        }

        @Override
        public String toString() {
            return "Attributes{" +
                    "attribute=" + attribute +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexSchema that = (IndexSchema) o;
        return Objects.equals(attributes, that.attributes)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, href);
    }

    @Override
    public String toString() {
        return "IndexSchema{" +
                "attributes=" + attributes +
                ", href=" + href +
                '}';
    }
}

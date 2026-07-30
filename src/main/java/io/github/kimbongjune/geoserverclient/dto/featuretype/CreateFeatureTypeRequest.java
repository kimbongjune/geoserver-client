package io.github.kimbongjune.geoserverclient.dto.featuretype;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for creating a feature type (with automatic table creation in H2/PostGIS).
 *
 * <pre>{@code
 * // Minimal (auto table creation in H2/PostGIS)
 * CreateFeatureTypeRequest.of("mypoints")
 *     .srs("EPSG:4326")
 *     .attribute("the_geom", "org.locationtech.jts.geom.Point")
 *     .attribute("name", "java.lang.String")
 *
 * // With title and abstract
 * CreateFeatureTypeRequest.of("mypoints")
 *     .srs("EPSG:4326")
 *     .title("My Points")
 *     .abstractText("A point layer")
 *     .attribute("the_geom", "org.locationtech.jts.geom.Point")
 *     .attribute("id", "java.lang.Integer")
 *     .attribute("name", "java.lang.String")
 * }</pre>
 */
public class CreateFeatureTypeRequest {

    private final String name;
    private String nativeName;
    private String title;
    private String abstractText;
    private String srs;
    private String projectionPolicy;
    private Boolean enabled;
    private final List<AttributeDef> attributes = new ArrayList<>();

    private CreateFeatureTypeRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "featureType name must not be null or empty");
        }
        this.name = name.trim();
    }

    public static CreateFeatureTypeRequest of(String name) {
        return new CreateFeatureTypeRequest(name);
    }

    /**
     * Alias for {@link #of(String)}, for callers who prefer the {@code builder(...)...build()}
     * spelling used by every {@code UpdateXxxRequest} in this library. {@link #build()} is a
     * no-op terminal call.
     */
    public static CreateFeatureTypeRequest builder(String name) {
        return of(name);
    }

    public CreateFeatureTypeRequest nativeName(String nativeName) {
        this.nativeName = nativeName;
        return this;
    }

    public CreateFeatureTypeRequest title(String title) {
        this.title = title;
        return this;
    }

    public CreateFeatureTypeRequest abstractText(String abstractText) {
        this.abstractText = abstractText;
        return this;
    }

    public CreateFeatureTypeRequest srs(String srs) {
        this.srs = srs;
        return this;
    }

    public CreateFeatureTypeRequest projectionPolicy(String projectionPolicy) {
        this.projectionPolicy = projectionPolicy;
        return this;
    }

    public CreateFeatureTypeRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Adds an attribute definition.
     *
     * @param attrName attribute name
     * @param binding  Java type binding (e.g. "org.locationtech.jts.geom.Point", "java.lang.String")
     */
    public CreateFeatureTypeRequest attribute(String attrName, String binding) {
        this.attributes.add(new AttributeDef(attrName, binding, null, null, null));
        return this;
    }

    /**
     * Adds an attribute definition with full options.
     *
     * @param attrName   attribute name
     * @param binding    Java type binding
     * @param minOccurs  minimum occurrences (null = server default)
     * @param maxOccurs  maximum occurrences (null = server default)
     * @param nillable   whether null is allowed (null = server default)
     */
    public CreateFeatureTypeRequest attribute(String attrName, String binding,
                                              Integer minOccurs, Integer maxOccurs, Boolean nillable) {
        this.attributes.add(new AttributeDef(attrName, binding, minOccurs, maxOccurs, nillable));
        return this;
    }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public CreateFeatureTypeRequest build() { return this; }

    public String getName()             { return name; }
    public String getNativeName()       { return nativeName; }
    public String getTitle()            { return title; }
    public String getAbstractText()     { return abstractText; }
    public String getSrs()              { return srs; }
    public String getProjectionPolicy() { return projectionPolicy; }
    public Boolean getEnabled()         { return enabled; }
    public List<AttributeDef> getAttributes() { return attributes == null ? null : Collections.unmodifiableList(attributes); }

    /** Definition of a single attribute. */
    public static class AttributeDef {
        private final String  name;
        private final String  binding;
        private final Integer minOccurs;
        private final Integer maxOccurs;
        private final Boolean nillable;

        public AttributeDef(String name, String binding,
                            Integer minOccurs, Integer maxOccurs, Boolean nillable) {
            this.name      = name;
            this.binding   = binding;
            this.minOccurs = minOccurs;
            this.maxOccurs = maxOccurs;
            this.nillable  = nillable;
        }

        public String  getName()      { return name; }
        public String  getBinding()   { return binding; }
        public Integer getMinOccurs() { return minOccurs; }
        public Integer getMaxOccurs() { return maxOccurs; }
        public Boolean getNillable()  { return nillable; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AttributeDef that = (AttributeDef) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(binding, that.binding)
                    && Objects.equals(minOccurs, that.minOccurs)
                    && Objects.equals(maxOccurs, that.maxOccurs)
                    && Objects.equals(nillable, that.nillable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, binding, minOccurs, maxOccurs, nillable);
        }

        @Override
        public String toString() {
            return "AttributeDef{" +
                    "name=" + name +
                    ", binding=" + binding +
                    ", minOccurs=" + minOccurs +
                    ", maxOccurs=" + maxOccurs +
                    ", nillable=" + nillable +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateFeatureTypeRequest that = (CreateFeatureTypeRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(title, that.title)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, title, abstractText, srs, projectionPolicy, enabled, attributes);
    }

    @Override
    public String toString() {
        return "CreateFeatureTypeRequest{" +
                "name=" + name +
                ", nativeName=" + nativeName +
                ", title=" + title +
                ", abstractText=" + abstractText +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", attributes=" + attributes +
                '}';
    }
}

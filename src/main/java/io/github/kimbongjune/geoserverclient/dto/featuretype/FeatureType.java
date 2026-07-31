package io.github.kimbongjune.geoserverclient.dto.featuretype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for feature type (vector layer) details.
 *
 * <p>Maps the response body of {@code GET /rest/workspaces/{ws}/datastores/{ds}/featuretypes/{ft}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureType {

    @JsonProperty("name")
    private String name;

    @JsonProperty("nativeName")
    private String nativeName;

    @JsonProperty("namespace")
    private NamespaceLink namespace;

    @JsonProperty("title")
    private String title;

    @JsonProperty("abstract")
    private String abstractText;

    @JsonProperty("keywords")
    private Keywords keywords;

    @JsonProperty("srs")
    private String srs;

    @JsonProperty("nativeBoundingBox")
    private BoundingBox nativeBoundingBox;

    @JsonProperty("latLonBoundingBox")
    private BoundingBox latLonBoundingBox;

    @JsonProperty("projectionPolicy")
    private String projectionPolicy;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("store")
    private StoreLink store;

    @JsonProperty("serviceConfiguration")
    private Boolean serviceConfiguration;

    @JsonProperty("maxFeatures")
    private Integer maxFeatures;

    @JsonProperty("numDecimals")
    private Integer numDecimals;

    @JsonProperty("padWithZeros")
    private Boolean padWithZeros;

    @JsonProperty("forcedDecimal")
    private Boolean forcedDecimal;

    @JsonProperty("overridingServiceSRS")
    private Boolean overridingServiceSRS;

    @JsonProperty("skipNumberMatched")
    private Boolean skipNumberMatched;

    @JsonProperty("circularArcPresent")
    private Boolean circularArcPresent;

    @JsonProperty("attributes")
    private Attributes attributes;

    /** Constructs an empty {@code FeatureType} for deserialization. */
    public FeatureType() {}

    /** @return the feature type name */
    public String getName() {
        return name;
    }
    /** @return the native name in the underlying store */
    public String getNativeName() {
        return nativeName;
    }
    /** @return the namespace link */
    public NamespaceLink getNamespace() {
        return namespace;
    }
    /** @return the human-readable title */
    public String getTitle() {
        return title;
    }
    /** @return the abstract description */
    public String getAbstractText() {
        return abstractText;
    }
    /** @return the keywords wrapper */
    public Keywords getKeywords() {
        return keywords;
    }
    /** @return the SRS identifier (e.g. {@code EPSG:4326}) */
    public String getSrs() {
        return srs;
    }
    /** @return the native bounding box */
    public BoundingBox getNativeBoundingBox() {
        return nativeBoundingBox;
    }
    /** @return the lat/lon bounding box */
    public BoundingBox getLatLonBoundingBox() {
        return latLonBoundingBox;
    }
    /** @return the projection policy */
    public String getProjectionPolicy() {
        return projectionPolicy;
    }
    /** @return {@code true} if the feature type is enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if enabled */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
    /** @return the store link */
    public StoreLink getStore() {
        return store;
    }
    /** @return {@code true} if service configuration is enabled */
    public Boolean getServiceConfiguration() {
        return serviceConfiguration;
    }
    /** @return the maximum number of features returned */
    public Integer getMaxFeatures() {
        return maxFeatures;
    }
    /** @return the number of decimal places */
    public Integer getNumDecimals() {
        return numDecimals;
    }
    /** @return {@code true} if zero-padding is enabled */
    public Boolean getPadWithZeros() {
        return padWithZeros;
    }
    /** @return {@code true} if forced decimal is enabled */
    public Boolean getForcedDecimal() {
        return forcedDecimal;
    }
    /** @return {@code true} if overriding service SRS */
    public Boolean getOverridingServiceSRS() {
        return overridingServiceSRS;
    }
    /** @return {@code true} if number matched is skipped */
    public Boolean getSkipNumberMatched() {
        return skipNumberMatched;
    }
    /** @return {@code true} if circular arcs are present */
    public Boolean getCircularArcPresent() {
        return circularArcPresent;
    }
    /** @return the attribute list wrapper */
    public Attributes getAttributes() {
        return attributes;
    }

    //  Inner DTOs 

    /** Namespace link (name + href) embedded in the feature type response. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamespaceLink {
        @JsonProperty("name")  private String name;
        @JsonProperty("href")  private String href;
        /** Constructs an empty {@code NamespaceLink} for deserialization. */
        public NamespaceLink() {}
        /** @return the namespace name */
        public String getName() {
            return name;
        }
        /** @return the namespace href */
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
            NamespaceLink that = (NamespaceLink) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, href);
        }

        @Override
        public String toString() {
            return "NamespaceLink{" +
                    "name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    /** Store link (class discriminator + name + href) embedded in the feature type response. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StoreLink {
        @JsonProperty("@class") private String storeClass;
        @JsonProperty("name")   private String name;
        @JsonProperty("href")   private String href;
        /** Constructs an empty {@code StoreLink} for deserialization. */
        public StoreLink() {}
        /** @return the store class discriminator */
        public String getStoreClass() {
            return storeClass;
        }
        /** @return the store name */
        public String getName() {
            return name;
        }
        /** @return the store href */
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
            StoreLink that = (StoreLink) o;
            return Objects.equals(storeClass, that.storeClass)
                    && Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(storeClass, name, href);
        }

        @Override
        public String toString() {
            return "StoreLink{" +
                    "storeClass=" + storeClass +
                    ", name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    /** Bounding box (minx, maxx, miny, maxy, crs) embedded in the feature type response. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoundingBox {
        @JsonProperty("minx") private Double minx;
        @JsonProperty("maxx") private Double maxx;
        @JsonProperty("miny") private Double miny;
        @JsonProperty("maxy") private Double maxy;
        @JsonProperty("crs")  private Object crs;
        /** Constructs an empty {@code BoundingBox} for deserialization. */
        public BoundingBox() {}
        /** @return the minimum x coordinate */
        public Double getMinx() {
            return minx;
        }
        /** @return the maximum x coordinate */
        public Double getMaxx() {
            return maxx;
        }
        /** @return the minimum y coordinate */
        public Double getMiny() {
            return miny;
        }
        /** @return the maximum y coordinate */
        public Double getMaxy() {
            return maxy;
        }
        /** @return the CRS value (string or object) */
        public Object getCrs() {
            return crs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BoundingBox that = (BoundingBox) o;
            return Objects.equals(minx, that.minx)
                    && Objects.equals(maxx, that.maxx)
                    && Objects.equals(miny, that.miny)
                    && Objects.equals(maxy, that.maxy)
                    && Objects.equals(crs, that.crs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(minx, maxx, miny, maxy, crs);
        }

        @Override
        public String toString() {
            return "BoundingBox{" +
                    "minx=" + minx +
                    ", maxx=" + maxx +
                    ", miny=" + miny +
                    ", maxy=" + maxy +
                    ", crs=" + crs +
                    '}';
        }
    }

    /** Keywords wrapper containing a list of keyword strings. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Keywords {
        @JsonProperty("string") private List<String> strings;
        /** Constructs an empty {@code Keywords} for deserialization. */
        public Keywords() {}
        /** @return the keyword strings */
        public List<String> getStrings() {
            return strings == null ? null : Collections.unmodifiableList(strings);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Keywords that = (Keywords) o;
            return Objects.equals(strings, that.strings);
        }

        @Override
        public int hashCode() {
            return Objects.hash(strings);
        }

        @Override
        public String toString() {
            return "Keywords{" +
                    "strings=" + strings +
                    '}';
        }
    }

    /** Attributes wrapper containing the list of attribute definitions. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {
        @JsonProperty("attribute") private List<Attribute> attribute;
        /** Constructs an empty {@code Attributes} for deserialization. */
        public Attributes() {}
        /** @return the attribute list */
        public List<Attribute> getAttribute() {
            return attribute == null ? null : Collections.unmodifiableList(attribute);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** A single attribute (field) definition within a feature type. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attribute {
        @JsonProperty("name")      private String  name;
        @JsonProperty("minOccurs") private Integer minOccurs;
        @JsonProperty("maxOccurs") private Integer maxOccurs;
        @JsonProperty("nillable")  private Boolean nillable;
        @JsonProperty("binding")   private String  binding;
        @JsonProperty("length")    private Integer length;

        /** Constructs an empty {@code Attribute} for deserialization. */
        public Attribute() {}
        /** @return the attribute name */
        public String  getName() {
            return name;
        }
        /** @return the minimum occurrence count */
        public Integer getMinOccurs() {
            return minOccurs;
        }
        /** @return the maximum occurrence count */
        public Integer getMaxOccurs() {
            return maxOccurs;
        }
        /** @return {@code true} if the attribute is nillable */
        public Boolean getNillable() {
            return nillable;
        }
        /** @return the fully-qualified Java binding class name */
        public String  getBinding() {
            return binding;
        }
        /** @return the field length */
        public Integer getLength() {
            return length;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Attribute that = (Attribute) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(minOccurs, that.minOccurs)
                    && Objects.equals(maxOccurs, that.maxOccurs)
                    && Objects.equals(nillable, that.nillable)
                    && Objects.equals(binding, that.binding)
                    && Objects.equals(length, that.length);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, minOccurs, maxOccurs, nillable, binding, length);
        }

        @Override
        public String toString() {
            return "Attribute{" +
                    "name=" + name +
                    ", minOccurs=" + minOccurs +
                    ", maxOccurs=" + maxOccurs +
                    ", nillable=" + nillable +
                    ", binding=" + binding +
                    ", length=" + length +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureType that = (FeatureType) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(title, that.title)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(keywords, that.keywords)
                && Objects.equals(srs, that.srs)
                && Objects.equals(nativeBoundingBox, that.nativeBoundingBox)
                && Objects.equals(latLonBoundingBox, that.latLonBoundingBox)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(store, that.store)
                && Objects.equals(serviceConfiguration, that.serviceConfiguration)
                && Objects.equals(maxFeatures, that.maxFeatures)
                && Objects.equals(numDecimals, that.numDecimals)
                && Objects.equals(padWithZeros, that.padWithZeros)
                && Objects.equals(forcedDecimal, that.forcedDecimal)
                && Objects.equals(overridingServiceSRS, that.overridingServiceSRS)
                && Objects.equals(skipNumberMatched, that.skipNumberMatched)
                && Objects.equals(circularArcPresent, that.circularArcPresent)
                && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, namespace, title, abstractText, keywords, srs, nativeBoundingBox, latLonBoundingBox, projectionPolicy, enabled, store, serviceConfiguration, maxFeatures, numDecimals, padWithZeros, forcedDecimal, overridingServiceSRS, skipNumberMatched, circularArcPresent, attributes);
    }

    @Override
    public String toString() {
        return "FeatureType{" +
                "name=" + name +
                ", nativeName=" + nativeName +
                ", namespace=" + namespace +
                ", title=" + title +
                ", abstractText=" + abstractText +
                ", keywords=" + keywords +
                ", srs=" + srs +
                ", nativeBoundingBox=" + nativeBoundingBox +
                ", latLonBoundingBox=" + latLonBoundingBox +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", store=" + store +
                ", serviceConfiguration=" + serviceConfiguration +
                ", maxFeatures=" + maxFeatures +
                ", numDecimals=" + numDecimals +
                ", padWithZeros=" + padWithZeros +
                ", forcedDecimal=" + forcedDecimal +
                ", overridingServiceSRS=" + overridingServiceSRS +
                ", skipNumberMatched=" + skipNumberMatched +
                ", circularArcPresent=" + circularArcPresent +
                ", attributes=" + attributes +
                '}';
    }
}

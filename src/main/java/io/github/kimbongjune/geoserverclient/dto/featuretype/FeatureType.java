package io.github.kimbongjune.geoserverclient.dto.featuretype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for feature type (vector layer) details.
 *
 * <p>Maps the response body of {@code GET /rest/workspaces/{ws}/datastores/{ds}/featuretypes/{ft}}.</p>
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

    public FeatureType() {}

    public String getName()                   { return name; }
    public String getNativeName()             { return nativeName; }
    public NamespaceLink getNamespace()       { return namespace; }
    public String getTitle()                  { return title; }
    public String getAbstractText()           { return abstractText; }
    public Keywords getKeywords()             { return keywords; }
    public String getSrs()                    { return srs; }
    public BoundingBox getNativeBoundingBox() { return nativeBoundingBox; }
    public BoundingBox getLatLonBoundingBox() { return latLonBoundingBox; }
    public String getProjectionPolicy()       { return projectionPolicy; }
    public Boolean getEnabled()               { return enabled; }
    public boolean isEnabled()                { return Boolean.TRUE.equals(enabled); }
    public StoreLink getStore()               { return store; }
    public Boolean getServiceConfiguration()  { return serviceConfiguration; }
    public Integer getMaxFeatures()           { return maxFeatures; }
    public Integer getNumDecimals()           { return numDecimals; }
    public Boolean getPadWithZeros()          { return padWithZeros; }
    public Boolean getForcedDecimal()         { return forcedDecimal; }
    public Boolean getOverridingServiceSRS()  { return overridingServiceSRS; }
    public Boolean getSkipNumberMatched()     { return skipNumberMatched; }
    public Boolean getCircularArcPresent()    { return circularArcPresent; }
    public Attributes getAttributes()         { return attributes; }

    //  Inner DTOs 

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamespaceLink {
        @JsonProperty("name")  private String name;
        @JsonProperty("href")  private String href;
        public NamespaceLink() {}
        public String getName() { return name; }
        public String getHref() { return href; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StoreLink {
        @JsonProperty("@class") private String storeClass;
        @JsonProperty("name")   private String name;
        @JsonProperty("href")   private String href;
        public StoreLink() {}
        public String getStoreClass() { return storeClass; }
        public String getName()       { return name; }
        public String getHref()       { return href; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoundingBox {
        @JsonProperty("minx") private Double minx;
        @JsonProperty("maxx") private Double maxx;
        @JsonProperty("miny") private Double miny;
        @JsonProperty("maxy") private Double maxy;
        @JsonProperty("crs")  private Object crs;
        public BoundingBox() {}
        public Double getMinx() { return minx; }
        public Double getMaxx() { return maxx; }
        public Double getMiny() { return miny; }
        public Double getMaxy() { return maxy; }
        public Object getCrs()  { return crs; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Keywords {
        @JsonProperty("string") private List<String> strings;
        public Keywords() {}
        public List<String> getStrings() { return strings == null ? null : Collections.unmodifiableList(strings); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {
        @JsonProperty("attribute") private List<Attribute> attribute;
        public Attributes() {}
        public List<Attribute> getAttribute() { return attribute == null ? null : Collections.unmodifiableList(attribute); }

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attribute {
        @JsonProperty("name")      private String  name;
        @JsonProperty("minOccurs") private Integer minOccurs;
        @JsonProperty("maxOccurs") private Integer maxOccurs;
        @JsonProperty("nillable")  private Boolean nillable;
        @JsonProperty("binding")   private String  binding;
        @JsonProperty("length")    private Integer length;

        public Attribute() {}
        public String  getName()      { return name; }
        public Integer getMinOccurs() { return minOccurs; }
        public Integer getMaxOccurs() { return maxOccurs; }
        public Boolean getNillable()  { return nillable; }
        public String  getBinding()   { return binding; }
        public Integer getLength()    { return length; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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

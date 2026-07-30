package io.github.kimbongjune.geoserverclient.dto.wmslayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for cascading WMS layer details.
 *
 * <p>Maps the response body of
 * {@code GET /rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer}}.</p>
 *
 * <p>Verified against GeoServer 2.28.2.</p>
 *
 * <h2>Known bugs (GeoServer 2.28.2)</h2>
 * <ul>
 *   <li>PUT {@code /wmslayers/{layer}} always returns 500 (WMSLayerInfo XStreamPersister bug)</li>
 *   <li>{@code selectedRemoteFormats} causes 500 on POST ("Duplicate field" error)</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WmsLayer {

    @JsonProperty("name")
    private String name;

    @JsonProperty("nativeName")
    private String nativeName;

    @JsonProperty("namespace")
    private NamespaceLink namespace;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("abstract")
    private String abstractText;

    @JsonProperty("keywords")
    private Keywords keywords;

    @JsonProperty("nativeCRS")
    private Object nativeCRS;

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

    @JsonProperty("forcedRemoteStyle")
    private String forcedRemoteStyle;

    @JsonProperty("preferredFormat")
    private String preferredFormat;

    @JsonProperty("minScale")
    private Double minScale;

    @JsonProperty("maxScale")
    private Double maxScale;

    @JsonProperty("metadataBBoxRespected")
    private Boolean metadataBBoxRespected;

    @JsonProperty("selectedRemoteStyles")
    private List<String> selectedRemoteStyles;

    @JsonProperty("vendorParameters")
    private VendorParameters vendorParameters;

    public WmsLayer() {}

    public String getName()                       { return name; }
    public String getNativeName()                 { return nativeName; }
    public NamespaceLink getNamespace()           { return namespace; }
    public String getTitle()                      { return title; }
    public String getDescription()               { return description; }
    public String getAbstractText()              { return abstractText; }
    public Keywords getKeywords()                { return keywords; }
    public Object getNativeCRS()                 { return nativeCRS; }
    public String getSrs()                       { return srs; }
    public BoundingBox getNativeBoundingBox()     { return nativeBoundingBox; }
    public BoundingBox getLatLonBoundingBox()     { return latLonBoundingBox; }
    public String getProjectionPolicy()           { return projectionPolicy; }
    public Boolean getEnabled()                  { return enabled; }
    public boolean isEnabled()                   { return Boolean.TRUE.equals(enabled); }
    public StoreLink getStore()                  { return store; }
    public Boolean getServiceConfiguration()      { return serviceConfiguration; }
    public String getForcedRemoteStyle()          { return forcedRemoteStyle; }
    public String getPreferredFormat()            { return preferredFormat; }
    public Double getMinScale()                   { return minScale; }
    public Double getMaxScale()                   { return maxScale; }
    public Boolean getMetadataBBoxRespected()     { return metadataBBoxRespected; }
    public List<String> getSelectedRemoteStyles() { return selectedRemoteStyles == null ? null : Collections.unmodifiableList(selectedRemoteStyles); }
    public VendorParameters getVendorParameters() { return vendorParameters; }

    // Inner DTOs

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamespaceLink {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;
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

    /**
     * WMS store link embedded in the layer response.
     * <pre>
     * {"@class": "wmsStore", "name": "ws:storeName", "href": "..."}
     * </pre>
     */
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

    /**
     * Bounding box. The {@code crs} field may be a plain string ("EPSG:4326") or an
     * object ({@code {"@class":"projected","$":"..."}}).
     */
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

    /**
     * Wrapper for {@code vendorParameters}.
     *
     * <p>The {@code entry} field in the GET response may be a single map or a list of maps:</p>
     * <pre>
     * Single: {"entry": {"@key": "K", "$": "V"}}
     * Multiple: {"entry": [{"@key": "K1", "$": "V1"}, ...]}
     * </pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VendorParameters {
        @JsonProperty("entry") private Object entry;
        public VendorParameters() {}
        /** Raw {@code entry} value — either a single {@code Map} or a {@code List<Map>}. */
        public Object getEntry() { return entry; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VendorParameters that = (VendorParameters) o;
            return Objects.equals(entry, that.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }

        @Override
        public String toString() {
            return "VendorParameters{" +
                    "entry=" + entry +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WmsLayer that = (WmsLayer) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(keywords, that.keywords)
                && Objects.equals(nativeCRS, that.nativeCRS)
                && Objects.equals(srs, that.srs)
                && Objects.equals(nativeBoundingBox, that.nativeBoundingBox)
                && Objects.equals(latLonBoundingBox, that.latLonBoundingBox)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(store, that.store)
                && Objects.equals(serviceConfiguration, that.serviceConfiguration)
                && Objects.equals(forcedRemoteStyle, that.forcedRemoteStyle)
                && Objects.equals(preferredFormat, that.preferredFormat)
                && Objects.equals(minScale, that.minScale)
                && Objects.equals(maxScale, that.maxScale)
                && Objects.equals(metadataBBoxRespected, that.metadataBBoxRespected)
                && Objects.equals(selectedRemoteStyles, that.selectedRemoteStyles)
                && Objects.equals(vendorParameters, that.vendorParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, namespace, title, description, abstractText, keywords, nativeCRS, srs, nativeBoundingBox, latLonBoundingBox, projectionPolicy, enabled, store, serviceConfiguration, forcedRemoteStyle, preferredFormat, minScale, maxScale, metadataBBoxRespected, selectedRemoteStyles, vendorParameters);
    }

    @Override
    public String toString() {
        return "WmsLayer{" +
                "name=" + name +
                ", nativeName=" + nativeName +
                ", namespace=" + namespace +
                ", title=" + title +
                ", description=" + description +
                ", abstractText=" + abstractText +
                ", keywords=" + keywords +
                ", nativeCRS=" + nativeCRS +
                ", srs=" + srs +
                ", nativeBoundingBox=" + nativeBoundingBox +
                ", latLonBoundingBox=" + latLonBoundingBox +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", store=" + store +
                ", serviceConfiguration=" + serviceConfiguration +
                ", forcedRemoteStyle=" + forcedRemoteStyle +
                ", preferredFormat=" + preferredFormat +
                ", minScale=" + minScale +
                ", maxScale=" + maxScale +
                ", metadataBBoxRespected=" + metadataBBoxRespected +
                ", selectedRemoteStyles=" + selectedRemoteStyles +
                ", vendorParameters=" + vendorParameters +
                '}';
    }
}

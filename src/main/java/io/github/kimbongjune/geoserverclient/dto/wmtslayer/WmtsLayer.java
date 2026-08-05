package io.github.kimbongjune.geoserverclient.dto.wmtslayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for cascading WMTS layer details.
 *
 * <p>Maps the response body of
 * {@code GET /rest/workspaces/{ws}/wmtsstores/{store}/layers/{layer}}.
 *
 * <p>Verified against GeoServer 2.28.2.
 *
 * <p><b>Differences from WmsLayer:</b><br>
 * WmtsLayer does not have WMS-specific fields ({@code forcedRemoteStyle}, {@code preferredFormat},
 * {@code minScale}, {@code maxScale}, {@code metadataBBoxRespected}, {@code selectedRemoteStyles},
 * {@code vendorParameters}). Instead, it includes the {@code advertised} field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WmtsLayer {

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
    private ProjectionPolicy projectionPolicy;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("advertised")
    private Boolean advertised;

    @JsonProperty("store")
    private StoreLink store;

    @JsonProperty("serviceConfiguration")
    private Boolean serviceConfiguration;

    @JsonProperty("metadata")
    private Metadata metadata;

    public WmtsLayer() {}

    public String getName() {
        return name;
    }
    public String getNativeName() {
        return nativeName;
    }
    public NamespaceLink getNamespace() {
        return namespace;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getAbstractText() {
        return abstractText;
    }
    public Keywords getKeywords() {
        return keywords;
    }
    public Object getNativeCRS() {
        return nativeCRS;
    }
    public String getSrs() {
        return srs;
    }
    public BoundingBox getNativeBoundingBox() {
        return nativeBoundingBox;
    }
    public BoundingBox getLatLonBoundingBox() {
        return latLonBoundingBox;
    }
    public ProjectionPolicy getProjectionPolicy() {
        return projectionPolicy;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
    public Boolean getAdvertised() {
        return advertised;
    }
    public StoreLink getStore() {
        return store;
    }
    public Boolean getServiceConfiguration() {
        return serviceConfiguration;
    }
    public Metadata getMetadata() {
        return metadata;
    }

    // Inner DTOs

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamespaceLink {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;
        public NamespaceLink() {}
        public String getName() {
            return name;
        }
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

    /**
     * Link to the parent WMTS store.
     * <pre>
     * {"@class": "wmtsStore", "name": "ws:storeName", "href": "..."}
     * </pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StoreLink {
        @JsonProperty("@class") private String storeClass;
        @JsonProperty("name")   private String name;
        @JsonProperty("href")   private String href;
        public StoreLink() {}
        public String getStoreClass() {
            return storeClass;
        }
        public String getName() {
            return name;
        }
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

    /**
     * Bounding box. The {@code crs} field may be a plain String ("EPSG:4326") or
     * an object ({@code {"@class":"projected","$":"..."}}).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoundingBox {
        @JsonProperty("minx") private Double minx;
        @JsonProperty("maxx") private Double maxx;
        @JsonProperty("miny") private Double miny;
        @JsonProperty("maxy") private Double maxy;
        @JsonProperty("crs")  private Object crs;
        public BoundingBox() {}
        public Double getMinx() {
            return minx;
        }
        public Double getMaxx() {
            return maxx;
        }
        public Double getMiny() {
            return miny;
        }
        public Double getMaxy() {
            return maxy;
        }
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Keywords {
        @JsonProperty("string") private List<String> strings;
        public Keywords() {}
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

    /**
     * Layer metadata entries.
     *
     * <p>Single entry: {@code {"entry": {"@key": "K", "$": "V"}}}<br>
     * Multiple entries: {@code {"entry": [{"@key": "K1", "$": "V1"}, ...]}}
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("entry") private Object entry;
        public Metadata() {}
        public Object getEntry() {
            return entry;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Metadata that = (Metadata) o;
            return Objects.equals(entry, that.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }

        @Override
        public String toString() {
            return "Metadata{" +
                    "entry=" + entry +
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
        WmtsLayer that = (WmtsLayer) o;
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
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(store, that.store)
                && Objects.equals(serviceConfiguration, that.serviceConfiguration)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, namespace, title, description, abstractText, keywords, nativeCRS, srs, nativeBoundingBox, latLonBoundingBox, projectionPolicy, enabled, advertised, store, serviceConfiguration, metadata);
    }

    @Override
    public String toString() {
        return "WmtsLayer{" +
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
                ", advertised=" + advertised +
                ", store=" + store +
                ", serviceConfiguration=" + serviceConfiguration +
                ", metadata=" + metadata +
                '}';
    }
}

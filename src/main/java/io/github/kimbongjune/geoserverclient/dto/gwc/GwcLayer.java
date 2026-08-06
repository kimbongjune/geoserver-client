package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a GWC tile layer ({@code GeoServerLayer}). Used for both GET and PUT.
 *
 * <p>Some fields ({@link #id}, {@link #inMemoryCached}, {@link #cacheWarningSkips})
 * are returned by GET but ignored on PUT ({@code WRITE_ONLY} annotation prevents serialization).
 *
 * <p>{@link #parameterFilters} is populated on GET. GWC returns these as
 * polymorphic XML elements ({@code styleParameterFilter}, {@code regexParameterFilter}, etc.)
 * that are difficult to round-trip on PUT — see
 * {@link io.github.kimbongjune.geoserverclient.api.gwc.GwcLayerManager} upsert for the recommended approach.
 *
 * <p>PUT must use {@code application/xml} — JSON PUT on GeoServer 2.28.2 causes an
 * XStream "Duplicate field" 500 error (see GwcLayerManager).
 */
@JacksonXmlRootElement(localName = "GeoServerLayer")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcLayer {

    private String name;
    private Boolean enabled;

    @JacksonXmlElementWrapper(localName = "mimeFormats")
    @JacksonXmlProperty(localName = "string")
    private List<String> mimeFormats;

    @JacksonXmlElementWrapper(localName = "gridSubsets")
    @JacksonXmlProperty(localName = "gridSubset")
    private List<GridSubset> gridSubsets;

    private Long expireCache;
    private Long expireClients;

    @JacksonXmlElementWrapper(localName = "metaWidthHeight")
    @JacksonXmlProperty(localName = "int")
    private List<Integer> metaWidthHeight;

    private Integer gutter;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean inMemoryCached;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JacksonXmlElementWrapper(localName = "cacheWarningSkips")
    @JacksonXmlProperty(localName = "string")
    private List<String> cacheWarningSkips;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JacksonXmlElementWrapper(localName = "parameterFilters")
    @JacksonXmlProperty(localName = "parameterFilter")
    private List<ParameterFilter> parameterFilters;

    /** Constructs an empty {@code GwcLayer} for deserialization. */
    public GwcLayer() {}

    /**
     * Constructs a {@code GwcLayer} with the given name.
     * @param name the layer name
     */
    public GwcLayer(String name) {
        this.name = name;
    }

    /** @return the layer name */
    public String getName() {
        return name;
    }
    /** @param name the layer name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return {@code true} if the layer is enabled for caching */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @param enabled {@code true} to enable */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /** @return the supported MIME formats */
    public List<String> getMimeFormats() {
        return mimeFormats == null ? null : Collections.unmodifiableList(mimeFormats);
    }
    /** @param mimeFormats the supported MIME formats */
    public void setMimeFormats(List<String> mimeFormats) {
        this.mimeFormats = mimeFormats;
    }

    /** @return the grid subsets */
    public List<GridSubset> getGridSubsets() {
        return gridSubsets == null ? null : Collections.unmodifiableList(gridSubsets);
    }
    /** @param gridSubsets the grid subsets */
    public void setGridSubsets(List<GridSubset> gridSubsets) {
        this.gridSubsets = gridSubsets;
    }

    /**
     * Fluent helper: sets gridSubsets from bare gridSetName strings.
     * @param gridSetNames the grid set names
     * @return this layer for chaining
     */
    public GwcLayer gridSetNames(String... gridSetNames) {
        List<GridSubset> subsets = new ArrayList<GridSubset>();
        for (String name : gridSetNames) {
            subsets.add(new GridSubset(name));
        }
        this.gridSubsets = subsets;
        return this;
    }

    /** @return the cache expiry time in seconds */
    public Long getExpireCache() {
        return expireCache;
    }
    /** @param expireCache the cache expiry time in seconds */
    public void setExpireCache(Long expireCache) {
        this.expireCache = expireCache;
    }

    /** @return the client-facing expiry time in seconds */
    public Long getExpireClients() {
        return expireClients;
    }
    /** @param expireClients the client expiry time in seconds */
    public void setExpireClients(Long expireClients) {
        this.expireClients = expireClients;
    }

    /** @return the metatile width and height {@code [width, height]} */
    public List<Integer> getMetaWidthHeight() {
        return metaWidthHeight == null ? null : Collections.unmodifiableList(metaWidthHeight);
    }
    /** @param metaWidthHeight the metatile dimensions {@code [width, height]} */
    public void setMetaWidthHeight(List<Integer> metaWidthHeight) {
        this.metaWidthHeight = metaWidthHeight;
    }

    /** @return the gutter size in pixels */
    public Integer getGutter() {
        return gutter;
    }
    /** @param gutter the gutter size in pixels */
    public void setGutter(Integer gutter) {
        this.gutter = gutter;
    }

    /** @return the internal layer ID (read-only) */
    public String getId() {
        return id;
    }
    /** @return {@code true} if the layer is in-memory cached (read-only) */
    public Boolean getInMemoryCached() {
        return inMemoryCached;
    }
    /** @return the cache warning skip list (read-only) */
    public List<String> getCacheWarningSkips() {
        return cacheWarningSkips == null ? null : Collections.unmodifiableList(cacheWarningSkips);
    }
    /** @return the parameter filters (read-only) */
    public List<ParameterFilter> getParameterFilters() {
        return parameterFilters == null ? null : Collections.unmodifiableList(parameterFilters);
    }

    /** A grid subset entry specifying which grid set this layer uses. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GridSubset {
        @JacksonXmlProperty(localName = "gridSetName")
        private String gridSetName;
        private Integer zoomStart;
        private Integer zoomStop;

        /** Constructs an empty {@code GridSubset} for deserialization. */
        public GridSubset() {}
        /**
         * Constructs a {@code GridSubset} with the given grid set name.
         * @param gridSetName the grid set name
         */
        public GridSubset(String gridSetName) {
            this.gridSetName = gridSetName;
        }

        /** @return the grid set name */
        public String getGridSetName() {
            return gridSetName;
        }
        /** @param gridSetName the grid set name */
        public void setGridSetName(String gridSetName) {
            this.gridSetName = gridSetName;
        }

        /** @return the minimum zoom level cached for this grid set (null = grid set default) */
        public Integer getZoomStart() {
            return zoomStart;
        }
        /** @param zoomStart the minimum zoom level to cache */
        public void setZoomStart(Integer zoomStart) {
            this.zoomStart = zoomStart;
        }

        /** @return the maximum zoom level cached for this grid set (null = grid set default) */
        public Integer getZoomStop() {
            return zoomStop;
        }
        /** @param zoomStop the maximum zoom level to cache */
        public void setZoomStop(Integer zoomStop) {
            this.zoomStop = zoomStop;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GridSubset that = (GridSubset) o;
            return Objects.equals(gridSetName, that.gridSetName)
                    && Objects.equals(zoomStart, that.zoomStart)
                    && Objects.equals(zoomStop, that.zoomStop);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gridSetName, zoomStart, zoomStop);
        }

        @Override
        public String toString() {
            return "GridSubset{" +
                    "gridSetName=" + gridSetName +
                    ", zoomStart=" + zoomStart +
                    ", zoomStop=" + zoomStop +
                    '}';
        }
    }

    /**
     * A parameter filter definition (e.g. {@code {"key": "STYLES", "defaultValue": ""}}).
     * These are read-only on GET; do not set them on PUT.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParameterFilter {
        private String key;
        private String defaultValue;

        /** @return the filter key (e.g. {@code "STYLES"}) */
        public String getKey() {
            return key;
        }
        /** @param key the filter key */
        public void setKey(String key) {
            this.key = key;
        }
        /** @return the default value for this filter */
        public String getDefaultValue() {
            return defaultValue;
        }
        /** @param defaultValue the default value */
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParameterFilter that = (ParameterFilter) o;
            return Objects.equals(key, that.key)
                    && Objects.equals(defaultValue, that.defaultValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, defaultValue);
        }

        @Override
        public String toString() {
            return "ParameterFilter{" +
                    "key=" + key +
                    ", defaultValue=" + defaultValue +
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
        GwcLayer that = (GwcLayer) o;
        return Objects.equals(name, that.name)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(mimeFormats, that.mimeFormats)
                && Objects.equals(gridSubsets, that.gridSubsets)
                && Objects.equals(expireCache, that.expireCache)
                && Objects.equals(expireClients, that.expireClients)
                && Objects.equals(metaWidthHeight, that.metaWidthHeight)
                && Objects.equals(gutter, that.gutter)
                && Objects.equals(id, that.id)
                && Objects.equals(inMemoryCached, that.inMemoryCached)
                && Objects.equals(cacheWarningSkips, that.cacheWarningSkips)
                && Objects.equals(parameterFilters, that.parameterFilters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, enabled, mimeFormats, gridSubsets, expireCache, expireClients, metaWidthHeight, gutter, id, inMemoryCached, cacheWarningSkips, parameterFilters);
    }

    @Override
    public String toString() {
        return "GwcLayer{" +
                "name=" + name +
                ", enabled=" + enabled +
                ", mimeFormats=" + mimeFormats +
                ", gridSubsets=" + gridSubsets +
                ", expireCache=" + expireCache +
                ", expireClients=" + expireClients +
                ", metaWidthHeight=" + metaWidthHeight +
                ", gutter=" + gutter +
                ", id=" + id +
                ", inMemoryCached=" + inMemoryCached +
                ", cacheWarningSkips=" + cacheWarningSkips +
                ", parameterFilters=" + parameterFilters +
                '}';
    }
}
